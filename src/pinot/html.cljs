(ns pinot.html
  (:referer :exclude [find])
  (:require [goog.dom :as dom]
            [goog.object :as gobj]
            [goog.dom.query :as query]
            [pinot.util.js :as pjs]))

(declare elem-factory)

(defn as-content [parent content]
  (doseq[c content]
    (let [child (cond
                  (nil? c) nil
                  (map? c) (throw "Maps cannot be used as content")
                  (string? c) (dom/createTextNode c)
                  (vector? c) (elem-factory c)
                  (coll? c) (as-content parent c))]
      (when child
        (dom/appendChild parent child)))))

(defn parse-tag [tag]
  (let [tag-str (name tag)]
    ;;TODO: handle cool tag names like :p#woot.cool
    tag-str
  ))

(defn set-attributes [elem attrs]
  (dom/setProperties elem (pjs/map->js attrs)))

(defn parse-content [elem content]
  (let [attrs (first content)]
  (if (map? attrs)
    (set-attributes elem attrs)
    content)))

(defn elem-factory [[tag & body]]
  (let [elem (dom/createElement (name tag))
        content (parse-content elem body)]
    (as-content elem content)
    elem))

(defn to-coll [c]
  (if (coll? c)
    c
    [c]))

(defn dom-clone [elem]
  (let [outer (dom/getOuterHtml elem)]
    (dom/htmlToDocumentFragment outer)))

;;TODO: for a collection of elements it appends the same DOM
;; element to each one, causing it just to move around. Really
;; if it's a collection we should clone the html element(s) and
;; append new ones to each of the items in elem. This, however,
;; makes it impossible to keep track of an individual dom fragment
;; for named view objects.
(defn append-to [elem html]
  (let [elem (to-coll elem)
        html (to-coll html)]
    (doseq [el elem
            tag html]
      (dom/appendChild el (dom-clone tag)))))

(defn html [& tags]
  (map elem-factory tags))

(defn range [s e]
  (take (- e s) (iterate inc s)))

(defn find [q]
  (let [results (dom/query q)
        len (.length results)]
    ;; The results are a nodelist, which looks like an array, but
    ;; isn't one. We have to turn it into a collection that we can
    ;; work with.
    (for [x (range 0 len)]
      (aget results x))))


(def items (html [:li "test"]
                 [:li "cool"]
                 [:li "yay!"]))
(append-to (find "body") (html [:ul {:id "list"}]))
(append-to (find "#list") items)
