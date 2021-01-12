(ns seven.components.helpers
  (:require [clojure.string :as s]))

(defn split-date [v]
  (s/split v #"/"))

(defn bad-format? [v]
  (or
   (not (= (map count (split-date v)) '(2 2 4)))
   (not (= ((frequencies v) (char 47)) 2))))
