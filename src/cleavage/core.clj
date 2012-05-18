(ns cleavage.core
  (:require [cleavage.repository :as repo]
	    [cleavage.code-analysis :as code]
	    [cleavage.glviewer :as glviewer]
	    [cleavage.git-repository :as git]))

(defrecord FileHistory [^String filename points])

(defn- normalize-coordinates
  [coordinates revision-count]
  (let [with-root (if (zero? (-> coordinates last last))
                    coordinates
                    (concat coordinates '([0 0 0])))]
    (if (= (-> with-root first last) revision-count)
      with-root
      (let [latest-commit (first with-root)] (conj with-root [(latest-commit 0) (latest-commit 1) revision-count])))))

(defn- file-and-commits
  [repository filename]
  (list filename (repo/commits repository filename "HEAD")))

(defn- coordinates
  [repository file-glob]
  (map #(let [filename (first file-glob)]
          (vector
           (->> % (repo/revision-contents repository filename) code/complexity)
           (->> % (repo/commits repository filename) count)
           (repo/revision-number repository %)))
       (last file-glob)))

(defn- history
  [dir]
  (let [repository (git/git-repository dir)]		      
    (map #(FileHistory.
           (first %)
           (normalize-coordinates (coordinates repository %) (-> repository repo/revisions count)))
         (map  #(file-and-commits repository %) (repo/files repository)))))

(defn cleavage [dir]
  (glviewer/start (history dir)))
