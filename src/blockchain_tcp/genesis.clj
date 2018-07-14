(ns blockchain-tcp.genesis
  (:require [utils :refer [now]]))

(defonce ^:private genesis-block {:index 0
                                  :timestamp (now)
                                  :transactions []
                                  :proof "vutr.io"
                                  :prev-hash "khoai"})

(defonce chain (atom [genesis-block]))

(defonce network-nodes (atom []))
