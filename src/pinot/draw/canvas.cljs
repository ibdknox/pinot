(ns pinot.draw.canvas
  (:require [pinot.html :as html]
            [pinot.dom :as dom]
            [pinot.draw.core :as draw]
            [pinot.util.js :as pjs]))

;;*********************************************
;; Canvas drawing functions
;;*********************************************

(defn get-context [canvas type]
  (. canvas (getContext (name type))))

(defn begin-path [ctx]
  (. ctx (beginPath))
  ctx)

(defn close-path [ctx]
  (. ctx (closePath))
  ctx)

(defn fill [ctx]
  (. ctx (fill))
  ctx)

(defn stroke [ctx]
  (. ctx (stroke))
  ctx)

(defn clear-rect [ctx {:keys [x y w h]}]
  (. ctx (clearRect x y w h))
  ctx)

(defn rounded-rect [ctx {:keys [x y w h radius]}]
  (let [r2d (/ Math/PI 180)]
    (begin-path ctx)

    (. ctx (moveTo (+ x radius) y))
    (. ctx (lineTo (- w radius) y))
    (. ctx (arc (- w radius) (+ y radius) radius (* r2d 270) (* r2d 360) false))
    (. ctx (lineTo w (- h radius)))
    (. ctx (arc (- w radius) (- h radius) radius (* r2d 0) (* r2d 90) false))
    (. ctx (lineTo (+ x radius) h))
    (. ctx (arc (+ x radius) (- h radius) radius (* r2d 90) (* r2d 180) false))
    (. ctx (lineTo x (+ y radius)))
    (. ctx (arc (+ x radius) (+ y radius) radius (* r2d 180) (* r2d 270) false))

    (close-path ctx)
    ctx))

(defn rect [ctx {:keys [x y w h radius] :as settings}]
  (if (and radius (< 0 radius))
    (rounded-rect ctx settings)
    (do (begin-path ctx)
        (. ctx (rec t x y w h))
        (close-path ctx)))
  ctx)

(defn fill-rect [ctx settings color]
  (-> ctx 
      (save)
      (rect settings)
      (fill-style color)
      (fill)
      (restore))
  ctx)

(defn circle [ctx {:keys [x y r]}]
  (begin-path ctx)
  (. ctx (arc x y r 0 (* (.PI js/Math) 2) true))
  (close-path ctx)
  (fill ctx)
  ctx)

(defn text [ctx {:keys [text x y]}]
  (. ctx (fillText text x y)))

(defn fill-style [ctx color]
  (set! ctx.fillStyle color)
  ctx)

(defn stroke-style [ctx color]
  (set! ctx.strokeStyle color)
  ctx)

(defn stroke-width [ctx w]
  (set! ctx.lineWidth w)
  ctx)

(defn move-to [ctx x y]
  (. ctx (moveTo x y))
  ctx)

(defn line-to [ctx x y]
  (. ctx (lineTo x y))
  ctx)

(defn alpha [ctx a]
  (set! ctx.globalAlpha a)
  ctx)

(defn save [ctx]
  (. ctx (save))
  ctx)

(defn restore [ctx]
  (. ctx (restore))
  ctx)

;;*********************************************
;; Canvas Entities
;;*********************************************

(def entities (atom {}))

(defn add-entity [k ent]
  (swap! entities assoc k ent))

(defn remove-entity [k]
  (swap! entities dissoc k))

(defn get-entity [k]
  (get-in @entities [k :value]))

(defn update-entity [k func & extra]
  (swap! entities (fn [ents]
                    (assoc ents k (apply func (get ents k) extra)))))

(defn entity [v update draw]
  {:value v
   :draw draw
   :update update})

(defn update-loop []
  (doseq[[k {:keys [update value]}] @entities]
    (when update
      (swap! entities assoc-in [k :value] (update value))))
  (js/setTimeout update-loop 10))

(defn draw-loop [ctx width height]
  (clear-rect ctx {:x 0 :y 0 :w width :h height})

  (doseq[[_ {:keys [draw value]}] @entities]
    (when draw
      (draw ctx value)))
    (draw/animation-frame #(draw-loop ctx width height)))

(defn init [canvas & [context-type]]
  (let [ct (or context-type "2d")
        width (dom/attr canvas :width)
        height (dom/attr canvas :height)
        ctx (get-context canvas ct)]
    (update-loop)
    (draw-loop ctx width height)))




