(ns pinot.util.clj)

(defn range [s e]
  (take (- e s) (iterate inc s)))
