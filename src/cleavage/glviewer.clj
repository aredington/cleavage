(ns cleavage.glviewer
  (:use [penumbra opengl]
        [penumbra.opengl.core :only [gl-import]])
  (:require [penumbra.app :as app]
	    [cleavage.fakescatter :as fake]))

;; -----------------------------------------------------------------------------
;; Vars

(def app-width 800)
(def app-height 600)

(def cos-quarter-pi (Math/cos (/ Math/PI 4)))

(defn oct-points
  [[x y z]]
  (let [z (/ z 10.0)
	y (* y 10.0)]
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
  (shade-model :smooth)
  (clear-color 0 0 0 0.5)
  (clear-depth 1)
  (enable :depth-test)
  (depth-test :lequal)
  (hint :perspective-correction-hint :nicest)
  (assoc state :fullscreen false)
  (merge state
	 {:xrot 0
	  :yrot 0}))

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
  [[first-point second-point]]
  (reduce concat (list (oct-points first-point) (reverse (oct-points second-point)))))

(defn triangle-strips
  "generate the triangle strips for a pair of points"
  [point-pair]
  (let [[v0 v1 v2 v3 v4 v5 v6 v7 v8 v9 v10 v11 v12 v13 v14 v15] (points point-pair)]
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
     v8  v15 v0
     ]))

(defn draw-tendril
  "draw a tendril for one file's complete history"
  [file]
  (push-matrix 
   (doseq [point-pair (partition 2 1 (:points file))]
     (draw-triangles (dorun (map #(apply vertex %) (triangle-strips point-pair)))))))

  (defn display [[delta time] state]
    (translate -4.0 -4.0 -20.0)
    (rotate (:xrot state) 1.0 0.0 0.0)
    (rotate (:yrot state) 0.0 0.0 1.0)
    (doseq [file (:files state)]
      (draw-tendril file))
    (app/repaint!))

(defn display-proxy [& args]
  (apply display args))

(def options {:reshape reshape
              :key-press key-press
              :display display
              :init init})

(defn start [history]
  (app/start options {:files history}))
