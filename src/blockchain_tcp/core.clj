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

;; See `two-way-handler`, `wrap-duplex-stream` and protocol is just convenient
;; wrapper to use Clojure's EDN message format

;; (def protocol
;;   (gloss/compile-frame
;;    (gloss/finite-frame :uint32
;;                        (gloss/string :utf-8))
;;    pr-str
;;    edn/read-string))

;; (defn wrap-duplex-stream
;;   [protocol s]
;;   (let [out (s/stream)]
;;     (s/connect
;;      (s/map #(io/encode protocol %) out)
;;      s)
;;     (s/splice
;;      out
;;      (io/decode-stream s protocol))))

;; (defn client
;;   [host port]
;;   (d/chain (tcp/client {:host host, :port port})
;;            #(wrap-duplex-stream protocol %)))

(defn -decode [bs]
  "Receive []bytes, we can use any message format we want, some popular formats are:
   - single line
   - multiple-line-based, i.e: HTTP
   - s-expressions
   - json
   - message-pack
   - and of course, edn
  "
  (String. bs))

(defn -encode [msg]
  "Reverse of `-decode`"
  (.getBytes msg))

(defn one-way-handler [s info]
  (println "--> new connection" info)
  (s/connect (s/map (comp println -decode) s) s))

(defn one-way-handler2 [s info]
  (println "--> new connection" info)
  ;; (s/consume (comp println -decode) s))
  (s/consume #(println (-decode %)) s))

(defn two-way-handler [s info]
  (println "--> new connection" info)
  (s/consume (fn [bs]
               (println (-decode bs))
               @(s/put! s (-encode (.concat "Reply: " (-decode bs)))))
             s))

(defn start-server
  [handler port]
  (tcp/start-server
   ;; handler = (fn [stream info])
   ;; we only need this if we use `wrap-duplex-stream` and/or protocol decoder
   ;; (fn [s info] (handler s info))
   handler
   {:port port}))

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
        ;; (start-server one-way-handler (:port opts))
        ;; (start-server one-way-handler2 (:port opts))
        (start-server two-way-handler (:port opts))
        )
      (catch Exception e
        (do (.printStackTrace e) (System/exit 0))))))
