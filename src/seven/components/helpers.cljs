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

;(defn bad-start-date? [d]
  ;(let [today (js/Date.) [day month year] (split-date d)]
    ;(print (.toDateString today) "today")
    ;(print (js/Date. year (- month 1) day))))

(defn bad-start-date? [d]
  (let [today (js/Date.) [month day year] (map int (split-date d))]
    (let [this-year (.getUTCFullYear today) this-month (.getUTCMonth today) this-day (.getUTCDate today)]
      (cond
        (or (< 1 day) (< 1 month)) true
        (< year this-year) true
        ; Inc the month, javascript months start at zero :|
        (and (< month (inc this-month)) (not (> year this-year))) true
        (and (= month (inc this-month)) (< day this-day)) true))))

(defn bad-return-date? [d1 d2]
  (let [[depart-month depart-day depart-year] (map int (split-date d1)) [return-month return-day return-year] (map int (split-date d2))]
    (cond
      (or (< 1 return-day) (< 1 return-month)) true
      (< return-year depart-year) true
        ; Inc the month, javascript months start at zero :|
      (and (< return-month depart-month) (not (> return-year depart-year))) true
      (and (= return-month depart-month)) (< return-day depart-day)) true))
