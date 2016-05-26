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

(register-handler
 :player-move-f
 (fn [db _]
   (update-in db [:board :player :row] inc)))

(register-sub
 :board-query
 (fn [db _]
   (reaction (:board @db))))

(keys/bind! "ctrl-f" ::move-f #(dispatch [:player-move-f]))
