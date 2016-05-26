(ns ecman.levels)

(defn tile [type row col]
  {:tile-type type :row row :col col})

(def level-1 [(tile :wall 0 0) (tile :wall 0 1) (tile :wall 0 2)
              (tile :start 1 0) (tile :path 1 1) (tile :exit 1 2)
              (tile :wall 2 0) (tile :wall 2 1) (tile :wall 2 2)])
