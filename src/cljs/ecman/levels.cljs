(ns ecman.levels)

(defn tile [tile-type row col]
  {:tile-type tile-type :row row :col col})

(def level-1
  [(tile :wall 0 0) (tile :wall 0 1) (tile :wall 0 2)
   (tile :start 1 0) (tile :path 1 1) (tile :exit 1 2)
   (tile :wall 2 0) (tile :wall 2 1) (tile :wall 2 2)])

(def level-2
  [(tile :wall 0 0) (tile :wall 0 1) (tile :wall 0 2) (tile :wall 0 3)
   (tile :start 1 0) (tile :path 1 1) (tile :path 1 2) (tile :exit 1 3)
   (tile :wall 2 0) (tile :wall 2 1) (tile :wall 2 2) (tile :wall 2 3)])

(def level-3
  [
   ;row1
   (tile :wall 0 0) (tile :wall 0 1) (tile :wall 0 2) (tile :wall 0 3)
   (tile :wall 0 4)
   ;row2
   (tile :start 1 0) (tile :path 1 1) (tile :path 1 2) (tile :path 1 3)
   (tile :exit 1 4)
   ;row3
   (tile :wall 2 0) (tile :wall 2 1) (tile :wall 2 2) (tile :wall 2 3)
   (tile :wall 2 4)])
