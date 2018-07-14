(ns blockchain-tcp.labors
  (:require [digest :refer [sha-256]]
            [utils :refer [get-values]]))

(defn- hashing-block [block]
  (sha-256 (apply str (get-values block))))

(defn- validate-proof [proof last-proof]
  (let [guess-hash (sha-256 (str proof last-proof))]
    (= (subs guess-hash 0 4) "0000")))

(defn- proof-of-work [last-proof]
  (loop [proof 0]
    (if-not (validate-proof proof last-proof)
      (recur (inc proof)) proof)))
