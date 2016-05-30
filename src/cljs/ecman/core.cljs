(ns ecman.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require
    [ecman.views :as views]
    [ecman.handlers :as handlers]
    [ecman.levels :as levels]
    [ecman.keyboard :as kb]
    [reagent.core :as r]
    [re-frame.core :refer [dispatch-sync] :as rf]
    [cljs.core.match :refer-macros [match]]
    ))

(enable-console-print!)

(defn js-println [& args]
  (->>
    (apply str args)
    (.log js/console)
    ))

(def initial-data
  {:board {:levels [levels/level-1 levels/level-2 levels/level-3]
           :level-ix 0
           :player {:row 1 :col 0}
           }})

(defn render! []
  (r/render [views/game-board] (.getElementById js/document "app")))

(def reload-fn! render!)

(defn initialize! []
  (dispatch-sync [:initialize initial-data])
  (render!))

(rf/register-handler
 :initialize
 (fn [_ [_ init-state]]
   (js-println "Level 1 and 3 keybinds: S-a, S-s, S-d, S-f, S-g")
   (js-println "Level 2 keybinds: l, gk, $")
   (js-println "Note: q resets combo. If commands aren't working you probably should reset the combo, though it resets itself at some point.")
   (ecman.keyboard/initialize!)
   init-state))

(defn key-combo-matches?! [db key-combo]
  ;(js-println (str "Combo: " key-combo))
  (let [dispatch #(do (rf/dispatch %) true)
        step-once #(dispatch [:step-once])
        teleport-to #(dispatch [:teleport-to %])
        level-ix (handlers/get-level-ix db)]
    (cond
      (not= level-ix 1)
      (do
        (match
          key-combo
          [{:code-name (:or :A :S :D :F :G) :shift? true}]
          (step-once)
          [_ _] true ; if combo is too long, reset.
          :else
          false ; no match
        ))
      :else
      (do
        (match
          key-combo
          [{:code-name :L :shift? false}]
          (step-once)
          [{:code-name :G :shift? false} ; not really a move-right vim keybind
           {:code-name :K :shift? false}]
          (step-once)
          [{:code-name :FOUR :shift? true}]
          (teleport-to (handlers/get-exit-tile (handlers/get-level db)))
          [_ _ _] true ; if combo is too long, reset.
          :else
          false ; no match
          ))
      )))

(rf/register-handler
  :key-press
  (kb/make-key-press-handler key-combo-matches?!))
