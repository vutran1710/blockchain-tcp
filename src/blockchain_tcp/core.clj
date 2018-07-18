(ns blockchain-tcp.core
  (:use [clojure.tools.cli])
  (:gen-class)
  (:require [manifold.deferred :as d]
            [manifold.stream :as s]
            [clojure.edn :as edn]
            [aleph.tcp :as tcp]
            [gloss.core :as gloss]
            [gloss.io :as io]
            [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]]))

(defonce tele (chan))

(def protocol
  (gloss/compile-frame
   (gloss/finite-frame :uint32
                       (gloss/string :utf-8))
   pr-str
   edn/read-string))

(defn wrap-duplex-stream
  [protocol s]
  (let [out (s/stream)]
    (s/connect
     (s/map #(io/encode protocol %) out)
     s)
    (s/splice
     out
     (io/decode-stream s protocol))))

(defn client
  [host port]
  (d/chain (tcp/client {:host host, :port port})
           #(wrap-duplex-stream protocol %)))

(defn start-server
  [handler port]
  (tcp/start-server
   (fn [s info] (handler s info))
   {:port port}))

(defn handler [st info]
  "TODO: make this shit talk!!!!"
  (println st)
  (println info))

(defn -main
  [& args]
  (let [[opts _ ban] (cli args
                          ["-p" "--port" "Port to listen to connections"
                           :default 10000 :parse-fn #(Integer/parseInt %)]
                          ["-h" "--help" "Show this help" :default false :flag true])]
    (when (:help opts)
      (do
        (println ban)
        (System/exit 0)))
    (try
      (do
        (println "Run on port: " (:port opts))
        (start-server handler (:port opts)))
      (catch Exception e
        (do (.printStackTrace e) (System/exit 0))))))
