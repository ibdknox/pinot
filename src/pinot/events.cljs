(ns pinot.events
  (:require [goog.events :as events]
            [pinot.util.clj :as pclj]
            [clojure.string :as string]))

(defn on [elem event func]
  (let [ev-name (string/upper-case (name event))]
    (doseq [el (pclj/->coll elem)]
      (events/listen el
                     (aget events/EventType ev-name)
                     (fn [e]
                       (func (.target e) e))))
    elem))
