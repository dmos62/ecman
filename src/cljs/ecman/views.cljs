(ns ecman.views
  (:require
   [reagent.ratom :as r]
   [re-frame.core :refer [subscribe] :as rf]))

(def tile-params {:width 40
                  :height 40
                  :tile-colors {:wall "grey"
                                :start "green"
                                :exit "orange"
                                :path "white"
                                :player "yellow"}})

(defn log [s] (when false (println s)))

(defn player-icon [location]
  (let [width (:width tile-params)
        radius (/ width 2)
        colors (:tile-colors tile-params)]
    (log "rendering player-icon")
    [:circle
     {:cy (+ (* (:row location) width) radius)
      :cx (+ (* (:col location) width) radius)
      :r radius
      :style {:fill (:player colors)}}]))

(defn tile-box [{:keys [tile-type row col]} tile]
  (let [width (:width tile-params)
        height (:height tile-params)
        colors (:tile-colors tile-params)]
    (when (= [1 1] [row col])
      (log "rendering tile at [1 1]."))
    [:rect
     {:x (* width col)
      :y (* height row)
      :height width
      :width height
      :style {:fill (get colors tile-type)
              :stroke-width 1
              :stroke "white"}}]))

(rf/register-sub
 :board-query
 (fn [db _]
   (r/reaction (:board @db))))

(defn game-board []
  (let [board (rf/subscribe [:board-query])]
    (fn []
      (log "rendering game-board")
      (let [board @board
            level-ix (:level-ix board)
            level (get-in board [:levels level-ix])]
        [:svg {:x 0 :y 0 :width 500 :height 500}
         (for [tile level]
           ^{:key tile}
           [tile-box tile])
         [player-icon (:player board)]
         ]))))
