(ns cleavage.test.glviewer
  (:use clojure.test
	cleavage.glviewer
	clojure.contrib.generic.math-functions))

(deftest percent-between-test
  (are [x y] (= x y)
       1/3 (percent-between 45 60 90) 
       2/3 (percent-between 90 60 45) ))

(deftest color-for-test
  (are [x y] (map #(approx= %1 %2 0.000000000001) x y)
       [1.0 0.0 0.0] (color-for [1.0 1.0 1.0] [1.0 1.0 1.0])
       [0.0 1.0 0.0] (color-for [0.0 1.0 234] [1.0 1.0 1/234])))

(deftest nth-percentile-test
  (are [x y] (= x y)
       99 (nth-percentile 99 (range 101))
       95 (nth-percentile 95 (range 101))
       95 (nth-percentile 95 (conj (range 100) 2377))))
