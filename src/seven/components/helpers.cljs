(ns seven.components.helpers
  (:require [clojure.string :as s]))

(defn split-date [v]
  (s/split v #"/"))

(defn contains-bad-chars? [v]

(defn bad-format? [v]
  (or
   (not (= (map count (split-date v)) '(2 2 4)))
  ; Char code 47 is a slash. Count them and make sure there are 2.
   (not (= ((frequencies v) (char 47)) 2))))
