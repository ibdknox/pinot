(ns pinot.events
  (:require [goog.events :as events]
            [pinot.util.clj :as pclj]
            [pinot.html :as html]
            [clojure.string :as string]))

(defn body (atom nil))

(defn get-body []
  (if-not @body
    (swap! body (first (html/dom-find "body")))
    @body))

(defn on [elem event func]
  (let [ev-name (string/upper-case (name event))]
    (doseq [el (pclj/->coll elem)]
      (events/listen (get-body)
                     (aget events/EventType ev-name)
                     (fn [e]
                       (let [elem-id (html/attr el :pinotId)
                             target-id (html/attr (.target e) :pinotId)]
                         (when (= elem-id target-id)
                           (func (.target e) e))))))
    elem))

(defn prevent [e]
  (. e (preventDefault)))
