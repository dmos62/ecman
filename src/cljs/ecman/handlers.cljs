(ns ecman.handlers
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [register-handler
                                   register-sub
                                   dispatch] :as rf]
            [ecman.levels :as levels]
            ))

(defn js-println [& args]
  (->>
    (apply str args)
    (.log js/console)
    ))

(defn get-exit-tile [level]
  (first (filter #(= (:tile-type %) :exit) level)))

(defn move [dir player]
  (let [f #(update-in player [%1] %2)]
    (case dir
      :up (f :row dec)
      :down (f :row inc)
      :right (f :col inc)
      :left (f :col dec)
      )))

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
  :move
  (fn [db [_ & subevent]]
    (when-not (:game-over db)
      (dispatch (vec subevent)))
    db
    ))

(defn teleport [player tile]
  (assoc player :row (:row tile) :col (:col tile)))

(defn possible? [db player]
  (let [{:keys [tile-type]} (levels/get-tile (get-level db) player)]
    (case tile-type
      (:wall nil) (do (js-println "Not possible.") nil)
      true
      )))

(defn player-change! [db new-player]
  (cond
    (possible? db new-player)
    (do
      (when (player-on-exit-tile? db new-player)
        (dispatch [:end-level]))
      (assoc-in db [:board :player] new-player))
    :else
    db
    ))

(register-handler
  :step-once
  (fn [db [_ dir]]
    (->>
      (move dir (get-player db))
      (player-change! db)
      )))

(register-handler
  :move-max
  (fn [db [_ dir]]
    (->>
      (loop [player (get-player db)]
        (let [moved (move dir player)]
          (if (possible? db moved)
            (recur moved)
            player)))
      (player-change! db)
      )))
