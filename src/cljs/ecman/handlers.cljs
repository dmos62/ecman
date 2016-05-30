(ns ecman.handlers
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [register-handler
                                   register-sub
                                   dispatch] :as rf]
            ))

(defn js-println [& args]
  (->>
    (apply str args)
    (.log js/console)
    ))

(defn get-exit-tile [level]
  (first (filter #(= (:tile-type %) :exit) level)))

(defn player-move-f [player]
  (update-in player [:col] inc))

(register-handler
 :end-game
 (fn [db _]
   (js-println "Game complete.")
   (assoc db :game-over true)
   ))

(defn get-all-levels [db]
  (get-in db [:board :levels]))

(defn get-level-ix [db]
  (get-in db [:board :level-ix]))

(defn get-level [db]
  (get (get-all-levels db) (get-level-ix db)))

(register-handler
  :load-level
  (fn [db [_ ix]]
    (js-println "Loading level " (inc ix))
    (->
      (assoc-in db [:board :level-ix] ix)
      (assoc-in [:board :player :col] 0))))

(register-handler
 :end-level
 (fn [db _]
   (js-println "Level complete.")
   (let [next-level (inc (get-level-ix db))
         last-level (dec (count (get-all-levels db)))]
     (if (> next-level last-level)
       (dispatch [:end-game])
       (dispatch [:load-level next-level]))
     db
     )))

(defn get-player [db] (get-in db [:board :player]))

(defn player-on-exit-tile? [db player]
  (let [exit-tile (get-exit-tile (get-level db))]
    (= player (select-keys exit-tile [:row :col]))))

(register-handler
  :step-once
  (fn [db _]
    (js-println "You make one step.")
    (if-not (:game-over db)
      (let [moved-player (player-move-f (get-player db))]
        (when (player-on-exit-tile? db moved-player)
          (dispatch [:end-level]))
        (assoc-in db [:board :player] moved-player))
      db
      )))

(defn teleport [player tile]
  (assoc player :row (:row tile) :col (:col tile)))

(register-handler
  :teleport-to
  (fn [db [_ tile]]
    (js-println "You've just teleported to end of line!")
    (if-not (:game-over db)
      (let [player (get-player db)
            teleported (teleport player tile)]
        (when (player-on-exit-tile? db teleported)
          (dispatch [:end-level]))
        (assoc-in db [:board :player] teleported))
      db
      )))
