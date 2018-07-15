(ns blockchain-tcp.worker
  (:require [digest :refer [sha-256]]
            [genesis :refer :all]
            [utils :refer [get-values now]]))

;; Private
(defn- hashing-block [block]
  (sha-256 (apply str (get-values block))))

(defn- validate-proof [proof last-proof]
  (let [guess-hash (sha-256 (str proof last-proof))]
    (= (subs guess-hash 0 4) "0000")))

(defn- proof-of-work [last-proof]
  (loop [proof 0]
    (if-not (validate-proof proof last-proof)
      (recur (inc proof)) proof)))

(defn- last-block [] (last @genesis/chain))
(defn- new-index [] (inc (:index (last-block))))


;; Public labors
(defn mine-new-block []
  (let [prev-hash (hashing-block (last-block))
        proof (proof-of-work (:proof (last-block)))]
    {:index (new-index)
     :time (now)
     :proof proof
     :previous-hash prev-hash}))

(defn validate-blocks [new-block old-block]
  "NOTE: Hybrid Proof-of-Work/Stake Blockchain"
  (and
   (= (:index new-block) (inc (:index old-block)))
   (= (:prev-hash new-block) (hashing-block old-block))
   (validate-proof (:proof new-block) (:proof old-block))))
