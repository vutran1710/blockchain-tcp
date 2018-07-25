(ns blockchain-tcp.protocol
  (:use [gloss.core] [gloss.io]))

;; Define protocol we are gonna give and receive
(def p (string :utf-8))
(def lp (string :utf-8 :delimiters ["\r\n"]))

(defcodec CHAIN ["CHAIN" p])
(defcodec MINE ["MINE" p])

(defcodec TCP_HEADER
  (header
   p
   (fn [h] (condp = h
             "CHAIN" CHAIN
             "MINE" MINE
             CHAIN))
   (fn [b] (first b))))
