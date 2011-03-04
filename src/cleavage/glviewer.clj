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

(def tri [[0 1 0]
            [-1 -1 0]
            [1 -1 0]])

(def oct [[0.0 1.0 0.0]
	  [cos-quarter-pi cos-quarter-pi 0.0]
	  [0.0 0.0 0.0]
	  
	  [cos-quarter-pi cos-quarter-pi 0.0]
	  [1.0 0.0 0.0]
	  [0.0 0.0 0.0]
	  
	  [1.0 0.0 0.0]
	  [cos-quarter-pi (- cos-quarter-pi) 0.0]
	  [0.0 0.0 0.0]
	  
	  [cos-quarter-pi (- cos-quarter-pi) 0.0]
	  [0.0 -1.0 0.0]
	  [0.0 0.0 0.0]
	  
	  [0.0 -1.0 0.0]
	  [(- cos-quarter-pi) (- cos-quarter-pi) 0.0]
	  [0.0 0.0 0.0]
	  
	  [(- cos-quarter-pi) (- cos-quarter-pi) 0.0]
	  [-1.0 0.0 0.0]
	  [0.0 0.0 0.0]
	  
	  [-1.0 0.0 0.0]
	  [(- cos-quarter-pi) cos-quarter-pi 0.0]
	  [0.0 0.0 0.0]
	  
	  [(- cos-quarter-pi) cos-quarter-pi 0.0]
	  [0.0 1.0 0.0]
	  [0.0 0.0 0.0]])

(def quad [[-1 1 0]
             [1 1 0]
             [1 -1 0]
             [-1 -1 0]])

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

(defn draw-octagon
  "draw an octagon"
  [x y z]
  (push-matrix 
   (translate x y z)
   (scale 0.5 0.5 1.0)
   (draw-triangles (dorun (map #(apply vertex %) oct)))))

(defn display [[delta time] state]
  (translate -4.0 -4.0 -20.0)
  (rotate (:xrot state) 1.0 0.0 0.0)
  (rotate (:yrot state) 0.0 0.0 1.0)
  (doseq [point (:points state)]
    (apply draw-octagon point))
  (app/repaint!))

(defn display-proxy [& args]
  (apply display args))

(def options {:reshape reshape
              :key-press key-press
              :display display
              :init init})

(defn point-set [scatter-point-set height]
  (map #(vector (:complexity %) (:commits %) height) scatter-point-set))

(defn glpoints [revision]
  (let [scatter-point-set (map #(:points %) revision)]
    (map #(point-set %1 %2) scatter-point-set (map #(/ % 10.0) (range)))))

(defn glmassage [history]
  (reduce concat (glpoints (reverse history))))

(defn start [history]
  (app/start options {:points (glmassage history)}))
