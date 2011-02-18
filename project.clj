(defproject cleavage "0.0.1-SNAPSHOT"
  :description "A 3d visualizer for measuring complexity vs. activity vs. time"
  :dependencies [[clojure "1.2.0"],
		 [org.eclipse.jgit/org.eclipse.jgit "0.10.1"],
		 [org.clojure/clojure-contrib "1.2.0"]
                 [penumbra "0.6.0-SNAPSHOT"]
                 [penumbra/lwjgl "2.4.2"]]
  :repositories {"jgit-repository" "http://download.eclipse.org/jgit/maven"})
