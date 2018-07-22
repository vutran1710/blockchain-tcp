(ns utils
  (:require [clojure.java.io :as javis]))

;; Consts
(defonce TCP_DEFAULT_PORT 10200)
(defn current-path [] (System/getProperty "user.dir"))
(defn portfile [] (str (current-path) "/.ports"))


;; Pure functions
(defn now []
  (quot (System/currentTimeMillis) 1000))

(defn get-values [a-map]
  (vec (map #(last %) (seq a-map))))

(defn read-nth-line [file line-number]
  (with-open [rdr (javis/reader file)]
    (nth (line-seq rdr) (dec line-number))))

(defn count-file-line [file]
  (with-open [rdr (javis/reader file)]
    (count (line-seq rdr))))

(defn append-to-file [content file]
  (spit file (str content "\n") :append true))

(defn parse-port [port-string]
  (try (read-string port-string)
       (catch Exception e (println "Invalid port string!"))))


;; Dirty functions // App functions
(defn write-port-to-file-and-return-it [file port]
  (do (append-to-file port file) port))

(defn generate-port []
  (let [file (portfile)]
    (let [lc (count-file-line file)]
      (if (> lc 0)
        (->> (read-nth-line file lc)
             parse-port
             inc
             (write-port-to-file-and-return-it file))
        (->> TCP_DEFAULT_PORT
             (write-port-to-file-and-return-it file))))))
