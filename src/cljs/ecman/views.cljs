(ns ecman.views
  (:require
   [reagent.core :as r]))


(def state (r/atom {:board {:tiles [:wall  :wall :wall
                                    :start :path :exit
                                    :wall  :wall :wall]
                            :player [0 1]}}))

(def tile-color {:wall "black"
                 :start "blue"
                 :exit "orange"
                 :path "white"})

(defn player-icon [[row col]]
  [:circle
   {:cx (+ (* row 20) 10) :cy (+ (* col 20) 10) :r 10
    :style {:fill "yellow"}}])

(defn tile-box [row col tile]
  [:rect
   {:x (* 21 col)
    :y (* 21 row)
    :height 20
    :width 20
    :style {:fill (get tile-color tile)}}])

(defn svg-board [board]
  (let [rows (partition 3 (:tiles board))]
    [:svg {:x 0 :y 0 :width 500 :height 500}
     (for [[rix row] (map-indexed vector rows)
           [cix tile] (map-indexed vector row)]
       (tile-box rix cix tile))
     (player-icon (:player board))]))

(defn home-page []
  (svg-board (:board @state)))
