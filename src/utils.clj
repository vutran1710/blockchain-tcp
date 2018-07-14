(ns utils)

(defn now []
  (quot (System/currentTimeMillis) 1000))

(defn value-vector [a-map]
  (vec (map #(last %) (seq a-map))))
