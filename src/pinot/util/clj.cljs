(ns pinot.util.clj)

(defn ->coll [c]
  (if (coll? c)
    c
    [c]))

