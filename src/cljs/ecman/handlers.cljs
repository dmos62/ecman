(ns ecman.handlers
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [register-handler
                                   register-sub
                                   dispatch] :as rf]
            ))

(def println2 #(.log js/console %))

(defn get-exit-tile [level]
  (first (filter #(= (:tile-type %) :exit) level)))

(defn player-move-f [player]
  (update-in player [:col] inc))

(register-handler
 :end-game
 (fn [db _]
   (println2 "Game complete.")
   db
   ))

(defn get-all-levels [db]
  (get-in db [:board :levels]))

(defn get-level-ix [db]
  (get-in db [:board :level-ix]))

(defn get-level [db]
  (get (get-all-levels db) (get-level-ix db)))

(register-handler
 :end-level
 (fn [db _]
   (println2 "Level complete.")
   (let [next-level (inc (get-level-ix db))
         last-level (dec (count (get-all-levels db)))]
     (if (> next-level last-level)
       (do (dispatch [:end-game]) db)
       (->
         (update-in db [:board :level-ix] inc)
         (assoc-in [:board :player :col] 0)
         )))))

(register-handler
 :player-move-f
 (fn [db _]
   (if (:level-active? db)
     (let [moved-player (player-move-f (get-in db [:board :player]))
           exit-tile (get-exit-tile (get-level db))]
       (if (= moved-player (select-keys exit-tile [:row :col]))
         (do
           (dispatch [:end-level])
           (assoc-in db [:board :player] moved-player))
         (assoc-in db [:board :player] moved-player)))
     db)))
