(ns ecman.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require
    [ecman.views :as views]
    [ecman.handlers :as handlers]
    [ecman.levels :as levels]
    [ecman.keyboard :as kb]
    [reagent.core :as r]
    [re-frame.core :refer [dispatch-sync]]
    [keybind.core]
    ))

(enable-console-print!)


(def initial-data {:board {:level levels/level-1
                           :player {:row 1
                                    :col 0}}
                   :level-active? true})

(defn render! []
  (r/render [views/game-board] (.getElementById js/document "app")))

(defn initialize! []
  (dispatch-sync [:initialize initial-data])
  (render!))

(def reload-fn! render!)
