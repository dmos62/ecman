(ns ecman.views
  (:require
   [reagent.core :as r]
   [ecman.player :as player]))


(def state (r/atom {:board {:tiles [:wall  :wall :wall
                                    :start :path :exit
                                    :wall  :wall :wall]
                            :player {:row 0
                                     :col 1}}}))

(def tile-params {:width 20
                  :height 20
                  :tile-colors {:wall "black"
                                :start "blue"
                                :exit "orange"
                                :path "white"
                                :player "yellow"}})

(defn player-icon [location]
  (let [width (:width tile-params)
        radius (/ width 2)
        colors (:tile-colors tile-params)]
    [:circle
     {:cx (+ (* (:row location) width) radius)
      :cy (+ (* (:col location) width) radius)
      :r radius
      :style {:fill (:player colors)}}]))

(defn tile-box [row col tile]
  (let [width (:width tile-params)
        height (:height tile-params)
        colors (:tile-colors tile-params)]
    [:rect
     {:key (gensym "key-")
      :x (* width col)
      :y (* height row)
      :height width
      :width height
      :style {:fill (get colors tile)
              :stroke-width 1
              :stroke "white"}}]))

(defn svg-board [board]
  (let [rows (partition 3 (:tiles board))]
    [:svg {:x 0 :y 0 :width 500 :height 500}
     (for [[rix row] (map-indexed vector rows)
           [cix tile] (map-indexed vector row)]
       (tile-box rix cix tile))
     (player-icon (:player board))]))

(defn home-page []
  (svg-board (:board @state)))

(player/init-moving! state)
