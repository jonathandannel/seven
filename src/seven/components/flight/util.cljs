(ns seven.components.flight.util
  (:require [clojure.string :as cs]))

(defn split-date [v]
  (cs/split v #"/"))

(defn bad-chars? [v]
  (if (not (= (count v) 0))
    (not (re-matches #"[0-9/]+" v))))

(defn bad-format? [v]
  (if (not (= (count v) 0))
    (or
     (not (= (map count (split-date v)) '(2 2 4)))
     (not (= ((frequencies v) (char 47)) 2)))))

(defn bad-start-date? [d]
  (let [today (js/Date.) [month day year] (map int (split-date d))]
    (let [this-year (.getUTCFullYear today)
          this-month (.getUTCMonth today)
          this-day (.getUTCDate today)]
      (cond
        (or (< day 1) (< month 1)) true
        (< year this-year) true
        (and (< month (inc this-month)) (< year this-year)) true
        (and (= year this-year) (and (= month (inc this-month)) (< day this-day))) true
        :else false))))

(defn bad-return-date? [d1 d2]
  (let [[depart-month depart-day depart-year] (map int (split-date d1))
        [return-month return-day return-year] (map int (split-date d2))]
    (cond
      (or (< return-day 1) (< return-month 1)) true
      (< return-year depart-year) true
      (and (< return-month depart-month) (< return-year depart-year)) true
      (and (= return-year depart-year)
           (and (= return-month depart-month) (< return-day depart-day))) true
      :else false)))
