(ns seven.components.helpers
  (:require [clojure.string :as s]))

(defn parse-date [v]
  (let [date (s/split v #"/")]
    (s/join date)))
