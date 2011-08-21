(ns pinot.events
  (:require [goog.events :as events]
            [pinot.util.clj :as pclj]
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
    (fn? elem) {:group-id (:pinot-group (meta elem))}
    (html/attr elem :pinot-id) {:elem elem :pinot-id (html/attr elem :pinot-id)}
    :else {:elem elem }))

(defn match? [{:keys [elem group-id pinot-id]} init-target]
  (loop [target init-target]
    (let [target-group (html/attr target :group-id)
          target-pinot (html/attr target :pinot-id)]
      (when (not= target (html/parent (get-body)))
        (if (or
              (and elem (= elem target))
              (and group-id (= group-id target-group))
              (and pinot-id (= pinot-id target-pinot)))
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
