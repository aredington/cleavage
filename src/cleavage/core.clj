(ns cleavage.core
  (:require [cleavage.repository :as repo]
	    [cleavage.code-analysis :as code]))

(defrecord ScatterPoint [^String filename ^int complexity ^int commits])

(defrecord ScatterPlot [^String revision points])

(defn scatter-point
  [dir file revision]
  (ScatterPoint. (repo/relative-path dir file)
		 (code/complexity (repo/revision-contents dir file revision))
		 (repo/commit-count dir file revision)))

(defn scatter-plot
  [dir revision]
  (ScatterPlot. revision
		(map #(scatter-point dir %1 revision) (repo/target-files dir))))

(defn history
  [dir]
  (map #(scatter-plot dir %) (repo/revisions dir)))
