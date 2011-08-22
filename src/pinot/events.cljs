(ns pinot.events
  (:require [goog.events :as events]
            [pinot.util.clj :as pclj]
            [pinot.util.js :as pjs]
            [pinot.html :as html]
            [clojure.string :as string]))

(def body (atom nil))

(defn get-body []
  (if-not @body
    (reset! body (first (html/dom-find "body")))
    @body))

;;TODO: this is ugly.
(defn ->target [elem]
  (cond
    (fn? elem) {:pinotGroup (html/attr (first (elem)) :pinotGroup)}
    (html/attr elem :pinotId) {:elem elem :pinotId (html/attr elem :pinotId)}
    :else {:elem elem }))

(defn match? [{:keys [elem pinotGroup pinotId]} init-target]
  (loop [target init-target]
    (let [target-group (html/attr target :pinotGroup)
          target-pinot (html/attr target :pinotId)]
      (when (not= target (html/parent (get-body)))
        (if (or
              (and elem (= elem target))
              (and pinotGroup (= pinotGroup target-group))
              (and pinotId (= pinotId target-pinot)))
          target
          (recur (html/parent target)))))))

(defn on [elem event func]
  (let [ev-name (string/upper-case (name event))
        event (aget events/EventType ev-name)
        body-elem (get-body)]
    (doseq [el (pclj/->coll elem)]
      (let [parsed (->target el)]
        (events/listen body-elem
                       event
                       (fn [e]
                         (let [target (.target e)]
                           (if-let [match (match? parsed target)]
                             (func match e)
                             true))))))
    elem))

(defn prevent [e]
  (. e (preventDefault)))
