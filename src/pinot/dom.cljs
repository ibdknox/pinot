(ns pinot.dom
  (:require [goog.dom :as dom]
            [goog.events :as events]
            [goog.style :as gstyle]
            [goog.dom.query :as query]
            [goog.dom.forms :as forms]
            [pinot.util.clj :as pclj]
            [pinot.util.js :as pjs])
  (:refer-clojure :exclude [replace]))

;; ********************************************
;; Pinot specific
;; ********************************************

(defn pinot-group [func]
  (when (fn? func)
    (let [elem (func)]
      (attr (first elem) :pinotGroup))))

;; ********************************************
;; Attribute manipulation
;; ********************************************

(defn css [elem k & [v]]
  (cond
    (map? k) (doseq [[prop value] k]
               (css elem prop value))
    (nil? v) (gstyle/getStyle elem (name k))
    :else (doseq [el (pclj/->coll elem)]
            (gstyle/setStyle el (name k) (name v))))
  elem)

(defn attr 
  ([elem attrs]
   (if-not (map? attrs)
     (. elem (getAttribute (name attrs)))
     (do
       (doseq [[k v] attrs]
         (attr elem k v))
       elem)))
  ([elem k v]
   (doseq [el (pclj/->coll elem)]
     (. el (setAttribute (name k) v)))
   elem))

(defn text [elem v]
  (doseq [el (pclj/->coll elem)]
    (dom/setTextContent el v))
  elem)

(defn val [elem & [v]]
  (let [elem (if (coll? elem)
               (first elem)
               elem)]
    (if v
      (do 
        (forms/setValue elem v)
        elem)
      (forms/getValue elem))))

;; ********************************************
;; Dom interaction functions
;; ********************************************

(defn parent [elem]
  (.parentNode elem))

(defn is-dom? [elem]
  (dom/isNodeLike elem))

(defn dom-clone [elem]
  (. elem (cloneNode true))) 

;;TODO: for a collection of elements it appends the same DOM
;; element to each one, causing it just to move around. Really
;; if it's a collection we should clone the html element(s) and
;; append new ones to each of the items in elem. This, however,
;; makes it impossible to keep track of an individual dom fragment
;; for named view objects.
(defn append [elem html]
  (doseq [el (pclj/->coll elem)
          tag (pclj/->coll html)]
    (dom/appendChild el (dom-clone tag))))

(defn unappend [elem]
  (doseq [elem (pclj/->coll elem)]
    (dom/removeNode elem)))

(defn replace [elem html]
  (let [p (parent (if (coll? elem) (first elem) elem))]
    (unappend elem)
    (append p html)))


(defn nodelist->coll [nl]
    ;; The results are a nodelist, which looks like an array, but
    ;; isn't one. We have to turn it into a collection that we can
    ;; work with.
  (for [x (range 0 (.length nl))]
    (aget nl x)))

(defn query [q]
  (let [q (if (fn? q)
            (str "[pinotGroup$=" (pinot-group q) "]")
            q)
        results (dom/query q)]
    (nodelist->coll results)))

