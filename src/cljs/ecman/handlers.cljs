(ns ecman.handlers
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [register-handler
                                   register-sub
                                   dispatch]]
            [keybind.core :as keys]))

(register-handler
 :initialize
 (fn [db [_ value]]
   (merge db value)))

(defn get-exit-tile [level]
  (first (filter #(= (:tile-type %) :exit) level)))

(defn player-move-f [player]
  (update-in player [:col] inc))

(register-handler
 :end-level
 (fn [db _]
   (assoc db :level-active? false)))

(register-handler
 :player-move-f
 (fn [db _]
   (if (:level-active? db)
     (let [moved-player (player-move-f (get-in db [:board :player]))
           exit-tile (get-exit-tile (get-in db [:board :level]))]
       (if (= moved-player (select-keys exit-tile [:row :col]))
         (do
           (dispatch [:end-level])
           (assoc-in db [:board :player] moved-player))
         (assoc-in db [:board :player] moved-player)))
     db)))

(register-sub
 :board-query
 (fn [db _]
   (reaction (:board @db))))

(keys/bind! "ctrl-f" ::move-f #(dispatch [:player-move-f]))
