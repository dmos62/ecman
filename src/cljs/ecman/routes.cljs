(ns ecman.routes
  (:require [ecman.views :as views]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros]))

(secretary/set-config! :prefix "#")

(def routes
  {"/" #(views/home-page)})

(defn add-route [path view]
  (secretary/defroute (str path) []
    (session/put! :current-page view)))

(defn init! []
  (doseq [[path view] routes]
    (add-route path view)))
