(ns pinot.draw.core)

(def animation-frame 
  (or (.requestAnimationFrame js/window)
      (.webkitRequestAnimationFrame js/window)
      (.mozRequestAnimationFrame js/window)
      (.oRequestAnimationFrame js/window)
      (.msRequestAnimationFrame js/window)
      (fn [callback] (js/setTimeout callback 17))))



