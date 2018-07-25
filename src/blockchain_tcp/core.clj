(ns blockchain-tcp.core
  (:use [gloss.core]
        [gloss.io]
        [clojure.java.io]
        [clojure.tools.cli])
  (:require [manifold.stream :as s]
            [manifold.deferred :as d]
            [aleph.tcp :as tcp]
            [blockchain-tcp.genesis :as bc]
            [blockchain-tcp.network :refer [connect-to-server]]
            [blockchain-tcp.utils :refer [generate-port TCP_DEFAULT_PORT]]
            [cheshire.core :refer [generate-string] :rename {generate-string jsonify}])
  (:gen-class))

(def error-msg "Fuck off!\n")

;; Tatics:
;; 1) Automate assignment of port everytime lein runs (auto-remove port from .port file too)
;; - Create a client, connect to any node in the existing network, then:
;; - Provide a local/stored chain, if there is
;; - Network resolve, return the valid chain
;; - Update to the latest chain
;; - Update the latest network-node
;; - Enter stake
;; - Mine, if desired
;; - Auto-remove port from .port when closing

(defn routine-handler [s]
  #(letfn [(resp [x] (s/put! s x))]
     (let [msg (String. %)]
       (println msg)
       (cond
         (= "CHAIN" msg) (resp (jsonify @bc/chain))
         :else (resp error-msg)))))

(defn master-handler [s info]
  (s/consume (routine-handler s) s))

(defn -main
  [& args]
  (let [[opts _ ban] (cli args
                          ["-p" "--port" "Port to listen to connections"
                           :default TCP_DEFAULT_PORT :parse-fn #(Integer/parseInt %)]
                          ["-h" "--help" "Show this help" :default false :flag true])]
    (when (:help opts)
      (println ban)
      (System/exit 0))
    (try
      (let [port (generate-port)]
        (connect-to-server port)
        (tcp/start-server master-handler {:port port}))
      (catch Exception e
        (do (.printStackTrace e)
            (System/exit 0))))))
