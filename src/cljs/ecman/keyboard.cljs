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

(def modifier-code-names #{:SHIFT :CTRL :ALT})

(defn make-key-press-handler [key-combo-matches?!]
  ; key-combo-matches?! must return true if matches, and false if not.
  (fn [db [_ key-press]]
    (let [unchanged-db db
          cleared-combo (dissoc db :incomplete-key-combo)]
      (cond
        ; repeat press?
        (:repeat? key-press)
        unchanged-db
        ; modifier press?
        (contains? modifier-code-names (:code-name key-press))
        unchanged-db
        ; clear combo state?
        (= :Q (:code-name key-press))
        cleared-combo
        :else
        (let [key-combo
              (->
                (:incomplete-key-combo unchanged-db)
                vec
                (conj key-press))]
          (if (key-combo-matches?! key-combo) 
            cleared-combo
            (assoc unchanged-db :incomplete-key-combo key-combo)
            ))))))
