(ns blockchain-tcp.core
  (:use [gloss.core]
        [gloss.io]
        [clojure.java.io]
        [clojure.tools.cli])
  (:require [manifold.stream :as s]
            [manifold.deferred :as d]
            [aleph.tcp :as tcp]
            [blockchain-tcp.genesis :as bc]
            [blockchain-tcp.utils :refer [generate-port]]
            [cheshire.core :refer [generate-string] :rename {generate-string jsonify}])
  (:gen-class))

(def error-msg "Fuck off!\n")

;; (defn start-up []
;;   "Create a client, connect to any node in the existing network, then:"
;;   "- Provide a local/stored chain, if there is"
;;   "- Network resolve, return the valid chain"
;;   "- Update to the latest chain"
;;   "- Update the latest network-node"
;;   "- Enter stake"
;;   "- Mine, if desired"
;;   (connect-to-server))
;; (-> (connect-to-server)
;;     (propose-local-chain)
;;     (update-chain-and-nodes)
;;     (request-stake)
;;     (stand-by)))

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
      (let [port (generate-port)]
        (tcp/start-server master-handler {:port port})
        (println (format "Node is up on Port: %s." port)))
      (catch Exception e
        (do (.printStackTrace e)
            (System/exit 0))))))
