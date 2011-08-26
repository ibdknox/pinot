(ns pinot.util.js)

(defn map->js [m]
  (let [out (js-obj)]
    (doseq [[k v] m]
      (aset out (name k) v))
    out))

(defn log [text]
  (. js/console (log text)))

(defn as-int [n]
  (js/parseInt n))
