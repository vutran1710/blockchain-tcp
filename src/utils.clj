(ns utils)

(defn now []
  (quot (System/currentTimeMillis) 1000))

(defn get-values [a-map]
  (vec (map #(last %) (seq a-map))))
