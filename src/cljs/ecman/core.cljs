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
(def println2 #(.log js/console %))

(def initial-data {:board {:levels [levels/level-1 levels/level-1]
                           :level-ix 0
                           :player {:row 1
                                    :col 0}}
                   :level-active? true})

(defn render! []
  (r/render [views/game-board] (.getElementById js/document "app")))

(defn initialize! []
  (dispatch-sync [:initialize initial-data])
  (render!))

(rf/register-handler
 :initialize
 (fn [_ [_ init-state]]
   (do (ecman.keyboard/initialize!))
   init-state))

(defn key-combo-matches?! [key-combo]
  (println2 (str "Combo: " key-combo))
  (let [dispatch-returning-true #(do (rf/dispatch %) true)]
    (match
      key-combo
      [{:code-name :F :shift? true}
       {:code-name :G :shift? true}]
      (dispatch-returning-true [:player-move-f])
      [_ _ _] true ; if combo is too long, reset.
      :else
      false ; no match
      )))

(rf/register-handler
  :key-press
  (kb/make-key-press-handler key-combo-matches?!))

(def reload-fn! render!)
