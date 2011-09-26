(ns pinot.draw.geometry)

;;**************************************************
;; Pathing functions
;;**************************************************

(defn distance [origin target]
  (let [dx (- (:x target) (:x origin))
        dy (- (:y target) (:y origin))
        dir-x (if (= 0 dx)
               dx
               (/ dx (Math/abs dx)))
        dir-y (if (= 0 dy)
               dy
               (/ dy (Math/abs dy)))
        dist (Math/sqrt (+ (Math/pow dx 2) (Math/pow dy 2)))]
    {:delta {:x dx :y dy}
     :dir {:x dir-x :y dir-y}
     :dist dist}))

;;**************************************************
;; Bounding functions
;;**************************************************

(defn bottom-right [{:keys [x y w h r]}]
  ;;Account for circles whose x and y represent the center
  ;;instead of the top left
  (if r
    {:x (+ x (/ w 2))
     :y (+ y (/ h 2))}
    {:x (+ x w)
     :y (+ y h)}))

(defn top-left [{:keys [x y w h r]}]
  ;;Account for circles whose x and y represent the center
  ;;instead of the top left
  (if r
    {:x (- x (/ w 2))
     :y (- y (/ h 2))}
    {:x x
     :y y}))

(defn in-radius? [origin obj radius]
  (let [{:keys [dist]} (distance origin obj)]
    (< dist radius)))

(defn collision? [obj obj2]
  (let [br (bottom-right obj)
        tl (top-left obj)
        br2 (bottom-right obj2)
        tl2 (top-left obj2)]
    (and 
      ;;If the tops are higher than the bottoms
      (and (< (:y tl) (:y br2))
           (< (:y tl2) (:y br)))
      ;;And the lefts are "lefter" than the rights
      (and (< (:x tl) (:x br2))
           (< (:x tl2) (:x br))))))

(defn contained? [container obj]
  (let [cbr (bottom-right container)
        ctl (top-left container)
        br (bottom-right obj)
        tl (top-left obj)]
    (and 
      (and (< (:x ctl) (:x tl))
           (< (:y ctl) (:y tl)))
      (and (> (:x cbr) (:x br))
           (> (:y cbr) (:y br))))))

(defn in-bounds? [obj x2 y2]
  (let [br (bottom-right obj)
        tl (top-left obj)]
    (and (< (:x tl) x2 (:x br))
         (< (:y tl) y2 (:y br)))))

