(ns ecman.core
  (:require
   [ecman.views :as views]
   [reagent.core :as r]))

(enable-console-print!)

(defn mount-root []
  (r/render [views/home-page] (.getElementById js/document "app")))
