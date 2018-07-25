(ns blockchain-tcp.network
  (:require [manifold.stream :as s]
            [manifold.deferred :as d]
            [clojure.edn :as edn]
            [aleph.tcp :as tcp]
            [gloss.core :as gloss]
            [gloss.io :as io]
            [blockchain-tcp.genesis :as bc]
            [blockchain-tcp.utils :refer [generate-port TCP_DEFAULT_PORT]]))


(def protocol
  (gloss/compile-frame
   (gloss/finite-frame :uint32
                       (gloss/string :utf-8))
   pr-str
   edn/read-string))

(defn wrap-duplex-stream [protocol s]
  (let [out (s/stream)]
    (s/connect
     (s/map #(io/encode protocol %) out)
     s)
    (s/splice
     out
     (io/decode-stream s protocol))))

(defn get-client [port host]
  (d/chain (tcp/client {:host host, :port port})
           #(wrap-duplex-stream protocol %)))

(defn inquire-port []
  (print "What port to communicate?: ")
  (flush)
  (-> (read-line) read-string))

(defn connect-to-server [port]
  (println "-- Node start at port: " port)
  (when (> port TCP_DEFAULT_PORT)
    (-> (inquire-port)
        (get-client "localhost")
        (deref)
        (s/put! "CHAIN"))))
