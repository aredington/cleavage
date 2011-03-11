(ns cleavage.core
  (:require [cleavage.repository :as repo]
	    [cleavage.code-analysis :as code]
	    [cleavage.glviewer :as glviewer]
	    [cleavage.git-repository :as git]))

(defrecord FileHistory [^String filename points])

(defn history
  [dir]
  (let [repository (cleavage.git-repository.GitRepository. dir)
	file-and-commits (fn [filename] (list filename (repo/commits repository filename "HEAD")))
	coordinates (fn [file-glob] (map #(vector
				     (code/complexity (repo/revision-contents repository (first file-glob) %))
				     (count (repo/commits repository (first file-glob) %))
				     (repo/revision-number repository %))
					 (nth file-glob 1)))]		      
    (map #(FileHistory. (first %) (coordinates %)) (map file-and-commits (repo/files repository)))))

(defn cleavage [dir]
  (glviewer/start (history dir)))
