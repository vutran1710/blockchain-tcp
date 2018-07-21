(ns blockchain-tcp.core
  (:use [gloss.core]
        [gloss.io]
        [clojure.java.io]
        [clojure.tools.cli])
  (:require [manifold.stream :as s]
            [manifold.deferred :as d]
            [aleph.tcp :as tcp]
            [blockchain-tcp.genesis :as bc]
            [cheshire.core :refer [generate-string] :rename {generate-string jsonify}])
  (:gen-class))

(def error-msg "Fuck off!\n")

(defn routine-handler [s]
  #(letfn [(resp [x] (s/put! s x) (s/close! s))]
     (let [msg (String. %)]
       (cond
         (= "CHAIN\r\n" msg) (resp (jsonify @bc/chain))
         :else (resp error-msg)))))

(defn master-handler [s info]
  (s/consume (routine-handler s) s))

(defn -main
  [& args]
  (let [[opts _ ban] (cli args
                          ["-p" "--port" "Port to listen to connections"
                           :default 10200 :parse-fn #(Integer/parseInt %)]
                          ["-h" "--help" "Show this help" :default false :flag true])]
    (when (:help opts)
      (println ban)
      (System/exit 0))
    (try
      (tcp/start-server master-handler {:port (:port opts)})
      (println (format "Node is up on PORT %s." (:port opts)))
      (catch Exception e
        (do (.printStackTrace e)
            (System/exit 0))))))
