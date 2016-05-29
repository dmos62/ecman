(ns ecman.keyboard
  (:require [goog.events]
            [clojure.set]
            [re-frame.core :as rf]
            )
  (:import [goog.events KeyCodes]
           [goog.events KeyHandler]
           ))

(def code-names (clojure.set/map-invert (js->clj KeyCodes)))

(defn parse-key-event [e]
  (let [code-name (keyword (get code-names (.-keyCode e)))
        repeat? (.-repeat e)
        shift? (.-shiftKey e)]
    {:code-name code-name :repeat? repeat? :shift? shift?}
    ))

(defn initialize! []
  (goog.events/listen
    (KeyHandler. js/window)
    KeyHandler.EventType.KEY
    #(rf/dispatch [:key-press (parse-key-event %)])
    ))

(rf/register-handler 
  :key-press
  (fn [db [_ {:keys [code-name repeat? shift?] :as key-press}]]
    (cond
      (and
        (not repeat?)
        shift?
        (= code-name :F))
      (rf/dispatch [:player-move-f])
      :else
      nil)
    db
    ))
