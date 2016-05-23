(ns ecman.player
  (:require
   [keybind.core :as keys]))

(defn move-forward [state]
  (swap! state update-in [:board :player :row] inc))

(defn init-moving! [state]
  (keys/bind! "ctrl-f" ::move-f #(move-forward state)))
