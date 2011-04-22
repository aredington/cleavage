(ns cleavage.code-analysis)

(defn cyclomatches
  [contents]
  (->> contents (re-seq #"for|if|while|case|catch|&&|\\\|\\\||\\\?") count))

(defn cyclomatic-complexity
  "returns the cyclomatic complexity for one file"
  [contents]
  (cyclomatches contents))

(defn complexity
  "returns a numeric complexity score for ssome file"
  [contents]
  (cyclomatic-complexity contents))
