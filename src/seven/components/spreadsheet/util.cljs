(ns seven.components.spreadsheet.util
  (:require [reagent.core :as r]
            [clojure.string :as s]))

(def operations
  {:sum #(apply + %)
   :sub #(apply - %)
   :mul #(apply * %)
   :div #(apply / %)})

(defn get-coord-value [values string]
  (let [cell (values (keyword string))]
    (or (get cell :computed) (get cell :value))))
  ;(get-in values [(keyword string) :value])))

(defn unpack-range [_range all-values]
  (let [values (s/split _range #":")
        letter (first (first values))
        start-row (subs (first values) 1)
        end-row (subs (last values) 1)]
    (reduce (fn [acc el]
              (let [value
                    (get-coord-value all-values (str letter el))]
                (if (> (count (str value)) 0)
                  (conj acc (int value))
                  acc))) []
            (range (int start-row) (inc (int end-row))))))

(defn get-real-values-from-args [args all-values]
  (reduce (fn [acc el]
            (if (s/includes? el ":")
              (into acc (unpack-range (s/trim el) all-values))
              ; else
              (if (> (count (str (get-coord-value all-values (s/trim el)))) 0)
                (conj acc (int (get-coord-value all-values (s/trim el))))
                acc))) [] args))

(defn get-affected-cells [args]
  (reduce (fn [acc el]
            (if (s/includes? el ":")
              (let [values (s/split el #":")
                    letter (first (first values))
                    start-row (subs (first values) 1)
                    end-row (subs (last values) 1)]
                (into acc
                      (map #(str letter %)
                           (range (int start-row) (inc (int end-row))))))
              (if (> (int el) 0)
                (conj acc (s/trim el))
                acc)))
          [] args))

(defn read-function [v all-values set-function-cells coord]
  (if (> (count v) 3)
    (if-let [op (get operations (keyword (subs v 1 4)))]
      (let [arg-start (inc (s/index-of v "("))
            arg-end (s/index-of v ")")]
        (if (and arg-start arg-end op)
          (let [args
                (s/split (s/trim (subs v arg-start arg-end)) #",")]
            (set-function-cells coord (get-affected-cells args))
            (op
             (vec (get-real-values-from-args args all-values)))))))))

(defn is-function [v]
  (= (first v) "="))
