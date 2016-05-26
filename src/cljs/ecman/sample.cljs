(ns ecman.sample
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require
   [ecman.views :as views]
   [reagent.core :as reagent]
   [re-frame.core :refer [register-handler
                          register-sub
                          dispatch
                          dispatch-sync
                          subscribe]]))

(def initial-state {:title "Click this title"})

(register-handler
 :initialize
 (fn [db _]
   (merge db initial-state)))

(register-handler
 :title-update
 (fn [db [_ value]]
   (assoc db :title value)))

(register-sub
 :title-query
 (fn [db _]
   (reaction (:title @db))))

(defn hello-world []
  (let [title (subscribe [:title-query])]
    (fn hello-world-render []
      [:h1 {:on-click #(dispatch [:title-update "Hello, Cosmos"])} @title])))

(defn mount-root
  []
  (dispatch-sync [:initialize])
  (reagent/render [hello-world]
                  (js/document.getElementById "app")))
