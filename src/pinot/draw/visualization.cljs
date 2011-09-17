(ns pinot.draw.visualization
  (:require [pinot.html :as html]
            [pinot.dom :as dom]
            [pinot.draw.core :as draw]
            [pinot.util.js :as pjs]))

(defn visual [data]
  {:data data
   :attr {}})

(defn attr [vis k v]
  (assoc-in vis [:attr k] v))

(defn elem [vis el]
  (assoc vis :elem el))

(defn apply-attr [elem attr d idx]
  (doseq [[k v] attr]
    (let [v (if (fn? v)
              (v d idx)
              v)]
      (dom/attr elem k v))))

(defn create-elem [elem d idx]
  (cond 
    (fn? elem) (elem d idx)
    (vector? elem) (html/html elem)
    (keyword? elem) (html/html [elem])
    :else elem))

(defn enter [{:keys [data attr elem]} method]
  (doseq [[idx d] (map-indexed vector data)]
    (let [cur (create-elem elem d idx)]
      (apply-attr cur attr d idx)
      (method cur))))

(defn select [query]
  (dom/query query))

(defn data [vis d]
  (assoc vis :data d))

(defn transition [elems dur]
  {:elems elems
   :dur dur})

(defn do-animation [{:keys [start-time dur tween] :as anim}]
  (let [now (. js/Date (now))
        elapsed (min (/ (- now start-time) dur) 1)]
    (tween elapsed)
    (when-not (= elapsed 1)
      (draw/animation-frame (partial do-animation anim)))))

(defn end-states [elems attr data]
  (for [[el d] (map list elems data)]
    (for [[k v] attr]
      (let [v (if (fn? v) (v d) v)
            init (pjs/as-int (dom/attr el k))
            delta (- v init)]
        [k init delta]))))

(defn tween [{:keys [elems attr data]}]
  (let [final (end-states elems attr data)
        tweens (map list elems final)]
    (fn [elapsed-perc]
      (doseq [[el attrs] tweens
              [k init delta] attrs]
        (let [elapsed-delta (* elapsed-perc delta)
              change (+ init elapsed-delta)]
          (dom/attr el k change))))))
  
(defn start [{:keys [dur] :as transition}]
  (let [now (. js/Date (now))]
    (do-animation {:start-time now
                   :dur dur
                   :tween (tween transition)})))

