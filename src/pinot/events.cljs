(ns pinot.events
  (:require [goog.events :as events]
            [pinot.util.clj :as pclj]
            [pinot.util.js :as pjs]
            [pinot.dom :as dom]
            [clojure.string :as string]))

(def body (atom nil))

(defn get-body []
  (if-not @body
    (reset! body (first (dom/query "body")))
    @body))

;;TODO: this is ugly.
(defn ->target [elem]
  (cond
    (fn? elem) {:pinotGroup (dom/attr (first (elem)) :pinotGroup)}
    (dom/attr elem :pinotId) {:elem elem :pinotId (dom/attr elem :pinotId)}
    :else {:elem elem }))

(defn match? [{:keys [elem pinotGroup pinotId]} init-target]
  (loop [target init-target]
    (when target
      (let [target-group (dom/attr target :pinotGroup)
            target-pinot (dom/attr target :pinotId)]
        (when (not= target (dom/parent (get-body)))
          (if (or
                (and elem (= elem target))
                (and pinotGroup (= pinotGroup target-group))
                (and pinotId (= pinotId target-pinot)))
            target
            (recur (dom/parent target))))))))

(defn make-listener [func parsed]
  (fn [e]
    (let [target (.target e)]
      (if-let [match (match? parsed target)]
        (func match e)
        true))))

(defn on [elem event func]
  (let [ev-name (string/upper-case (name event))
        event (aget events/EventType ev-name)
        body-elem (get-body)]
    (doseq [el (pclj/->coll elem)]
      (let [parsed (->target el)]
        (events/listen body-elem
                       event
                       (make-listener func parsed))))
    elem))

(defn prevent [e]
  (. e (preventDefault)))
