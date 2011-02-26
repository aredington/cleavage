(ns cleavage.code-analysis)

(defn cyclomatches
  [contents]
  (count (re-seq #"for|if|while|case|catch|&&|\\\|\\\||\\\?" contents)))

(defn cyclomatic-complexity
  "returns the cyclomatic complexity for one file"
  [contents]
  (cyclomatches contents))

(defn complexity
  "returns a numeric complexity score for ssome file"
  [contents]
  (cyclomatic-complexity contents))
