(defproject blockchain-tcp "0.1.0-SNAPSHOT"
  :description "Blockchain tcp network, retake"
  :url "http://vutr.io/projects/blockchain"
  :license {:name "vutr"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [digest "1.4.8"]
                 [aleph "0.4.6" ]
                 [cheshire "5.8.0"]
                 [gloss "0.2.6"]
                 [org.clojure/tools.cli "0.3.7"]
                 [org.clojure/core.async "0.4.474"]]
  :main ^:skip-aot blockchain-tcp.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
