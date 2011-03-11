(ns cleavage.glviewer
  (:use [penumbra opengl]
        [penumbra.opengl.core :only [gl-import]])
  (:require [penumbra.app :as app]
	    [cleavage.fakescatter :as fake]
	    [clojure.contrib.generic.math-functions :as math]))

;; -----------------------------------------------------------------------------
;; Vars

(def app-width 800)
(def app-height 600)

(def cos-quarter-pi (Math/cos (/ Math/PI 4)))

(defn rtod
  [radians]
  (* 180 (/ radians Math/PI)))

(defn oct-points
  [[x y z] [x-scale y-scale z-scale]]
  (let [x (* x x-scale)
	y (* y y-scale)
	z (* z z-scale)]
    (vector [x (+ 1.0 y) z]
	    [(+ cos-quarter-pi x) (+ cos-quarter-pi y) z]
	    [(+ 1.0 x) y z]
	    [(+ cos-quarter-pi x) (- y cos-quarter-pi) z]
	    [x (+ -1.0 y) z]
	    [(- x cos-quarter-pi) (- y cos-quarter-pi) z]
	    [(+ -1.0 x) y z]
	    [(- x cos-quarter-pi) (+ y cos-quarter-pi) z])))

;; -----------------------------------------------------------------------------
;; Import

(gl-import glClearDepth clear-depth)

;; -----------------------------------------------------------------------------
;; Fns

(defn init [state]
  (app/title! "Cleavage GLViewer")
  (app/vsync! false)
  (app/display-mode! app-width app-height)
  (app/key-repeat! true)
  (shade-model :smooth)
  (clear-color 0 0 0 0.5)
  (clear-depth 1)
  (enable :depth-test)
  (depth-test :lequal)
  (hint :perspective-correction-hint :nicest)
  (assoc state :fullscreen false)
  (merge state
	 {:xrot -60
	  :yrot 45}))

(defn reshape [[x y width height] state]
  (viewport 0 0 app-width app-height)
  (frustum-view 45 (/ (double app-width) app-height) 0.1 100)
  (load-identity)
  state)

(defn key-press [key state]
  (condp = key
      :f1 state
      :up (update-in state [:xrot] #(+ % 1))
      :down (update-in state [:xrot] #(- % 1))
      :left (update-in state [:yrot] #(- % 1))
      :right (update-in state [:yrot] #(+ % 1))
      state))

(defn points
  "generate all points for a pair of coordinates"
  [[first-point second-point] normalization-vector]
  (reduce concat (list (oct-points first-point normalization-vector) (reverse (oct-points second-point normalization-vector)))))

(defn triangle-strips
  "generate the triangle strips for a pair of points"
  [point-pair normalization-vector]
  (let [[v0 v1 v2 v3 v4 v5 v6 v7 v8 v9 v10 v11 v12 v13 v14 v15] (points point-pair normalization-vector)]
    [v0  v1  v15
     v15 v14 v1
     v1  v2  v14
     v14 v13 v2
     v2  v3  v13
     v13 v12 v3
     v3  v4  v12
     v12 v11 v4
     v4  v5  v11
     v11 v10 v5
     v5  v6  v10
     v10 v9  v6
     v6  v7  v9
     v9  v8  v7
     v7  v0  v8
     v8  v15 v0]))

(defn nth-percentile
  "returns the item from coll within which n percent of
all other items fall below"
  [n coll]
  (let [sorted-coll (sort coll)
	count-coll (count coll)]
    (nth sorted-coll (* (/ n 100) count-coll))))

(defn normalization-vector
  "returns a vector of scaling multiples to apply to all coordinates
to fit them into a 10x10x10 cube"
  [history]
  (let [all-points (reduce concat (map #(:points %) history))
	nth-max (fn [n] (nth-percentile 95 (map #(nth % n) all-points)))]
    (vec (map #(/ 10.0 %) (map nth-max (range 3))))))

(defn draw-tendril
  "draw a tendril for one file's complete history"
  [file normalization-vector]
  (push-matrix 
   (doseq [point-pair (partition 2 1 (:points file))]
     (draw-triangles (dorun (map #(apply vertex %)
				 (triangle-strips point-pair normalization-vector)))))))

(defn draw-axes [] nil)

(defn percent-between
  [a b c]
  (/ (math/abs (- b a)) (math/abs (- c a))))

(defn color-for
  "calculate a color based on relative angle
to the origin after normalization is applied. 45deg = red,
90deg = blue, 0deg = green, corresponding to the 'refactor',
'cowboy' and 'fertile' territories respectively"
  [point normalization-vector]
  (let [normalized-point (take 2 (map * point normalization-vector))
	[x y _] normalized-point
	distance (math/sqrt (+ (math/sqr x) (math/sqr y)))
	unit-point (map #(/ % distance) normalized-point)
	angle (rtod (math/acos (nth unit-point 0)))]
    (if (> angle 45)
      [(percent-between 90 angle 45) 0.0 (percent-between 45 angle 90)]
      [(percent-between 0 angle 45) (percent-between 45 angle 0) 0.0])))

(defn display [[delta time] state]
  (let [normalization-vector (normalization-vector (:files state))]
    (translate -4.0 -4.0 -20.0)
    (rotate (:xrot state) 1.0 0.0 0.0)
    (rotate (:yrot state) 0.0 0.0 1.0)
    (draw-axes)
    (doseq [file (:files state)]
      (apply color (color-for (first (:points file)) normalization-vector))
      (draw-tendril file normalization-vector))
    (app/repaint!)))
    
(defn display-proxy [& args]
  (apply display args))

(def options {:reshape reshape
              :key-press key-press
              :display display
              :init init})

(defn start [history]
  (app/start options {:files history}))
