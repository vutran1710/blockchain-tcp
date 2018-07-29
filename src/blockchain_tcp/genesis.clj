(ns blockchain-tcp.genesis
  (:require [blockchain-tcp.utils :refer [now]]))

(defonce ^:private genesis-block {:index 0
                                  :timestamp (now)
                                  :transactions []
                                  ;; lets trade love instead of the emotionless coins ^_^
                                  :love 10
                                  :proof "vutr.io"
                                  :prev-hash "khoai"
                                  :validator "khoai"})

(defonce chain (atom [genesis-block]))

(defonce network-node (atom []))

(defonce stake (atom 0))
