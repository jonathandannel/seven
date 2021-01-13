(ns seven.components.helpers
  (:require [clojure.string :as s]))

(defn split-date [v]
  (s/split v #"/"))

(defn bad-chars? [v]
  (if (not (= (count v) 0))
    (not (re-matches #"[0-9/]+" v))))

(defn bad-format? [v]
  (if (not (= (count v) 0))
    (or
     (not (= (map count (split-date v)) '(2 2 4)))
     (not (= ((frequencies v) (char 47)) 2)))))

(defn bad-start-date? [d]
  (let [today (js/Date.) input (split-date d)]
    (print (.toDateString today))
    (print input)))
    ;(print (js/Date (input 2)  (- (input 0) 1) (- (input 1) 1)))))

(defn bad-return-date? [d1 d2]
  (print d1 d2 "bad return date"))
