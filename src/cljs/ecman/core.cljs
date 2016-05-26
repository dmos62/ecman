(ns ecman.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require
   [ecman.views :as views]
   [ecman.handlers :as handlers]
   [reagent.core :as r]
   [re-frame.core :refer [dispatch-sync]]))

(enable-console-print!)

(def initial-data {:board {:tiles [:wall  :wall :wall
                                   :start :path :exit
                                   :wall  :wall :wall]
                           :player {:row 0
                                    :col 1}}})

(defn mount-root []
  (dispatch-sync [:initialize initial-data])
  (r/render [views/home-page] (.getElementById js/document "app")))
