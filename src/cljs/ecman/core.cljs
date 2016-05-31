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
  (js-println "Re/starting game.")
  (rf/dispatch [:initialize initial-data])
  (render!))

(rf/register-handler
 :initialize
 (fn [_ [_ init-state]]
   (js-println "Level 1 and 3 keybinds: left: S-a, S-s. right: S-d, S-f.")
   (js-println "Level 2 keybinds: h, l, gj, gk, $, ^.")
   (js-println "Note: q resets combo. If commands aren't working you probably should reset the combo, though it resets itself if running combo is too long.")
   (js-println "Note: r restarts game.")
   (ecman.keyboard/initialize!)
   init-state))

(defn key-combo-matches?! [db key-combo]
  ;(js-println (str "Combo: " key-combo))
  (let [dispatch #(do (rf/dispatch %) true)
        step-once #(dispatch [:move :step-once %])
        move-max #(dispatch [:move :move-max %])
        level-ix (handlers/get-level-ix db)]
    (cond
      (= :R (:code-name (last key-combo)))
      (initialize!) ; restart game
      (not= level-ix 1)
      (do
        (match
          key-combo
          [{:code-name (:or :A :S) :shift? true}]
          (step-once :left)
          [{:code-name (:or :D :F) :shift? true}]
          (step-once :right)
          [_ _] true ; if combo is too long, reset.
          :else
          false ; no match
        ))
      :else
      (do
        (match
          key-combo
          [{:code-name no-shift :shift? false}]
          (case no-shift
            :L (step-once :right)
            :H (step-once :left)
            false)
          [{:code-name :G :shift? false} ; not really a move-right vim keybind
           {:code-name g-key :shift? false}]
          (case g-key
            :K (step-once :right)
            :J (step-once :left)
            false)
          [{:code-name shift-key :shift? true}]
            (case shift-key
              :SIX (move-max :left)
              :FOUR (move-max :right)
              false)
          [_ _ _] true ; if combo is too long, reset.
          :else
          false ; no match
          ))
      )))

(rf/register-handler
  :key-press
  (kb/make-key-press-handler key-combo-matches?!))
