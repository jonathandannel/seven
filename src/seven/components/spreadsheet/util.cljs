(ns seven.components.spreadsheet.util
  (:require [clojure.string :as cs]))

(def operations
  {:sum #(apply + %)
   :sub #(apply - %)
   :mul #(apply * %)
   :div #(apply / %)
   :avg #(/ (apply + %) (count %))})

; TODO: Only apply operations on numbers. Skip if contains non-number values.
; Right now we get NaN displaying if we try to add a number to a string.

(defn is-function [v]
  (= (first v) "="))

(defn get-coord-value [values string]
  (let [cell (values (keyword (cs/lower-case string)))]
    (or (get cell :computed) (get cell :value))))

; Return a list of coord values in string range
; Ex: "b1:b4" -> [12, 200, 5.5, 75]
(defn unpack-range [_range all-values]
  (let [values (cs/split _range #":")
        letter (first (first values))
        start-row (subs (first values) 1)
        end-row (subs (last values) 1)]
    (reduce (fn [acc el]
              (let [value
                    (get-coord-value all-values (str letter el))]
                (if (> (count (str value)) 0)
                  (conj acc (js/parseFloat value))
                  acc))) []
            (range (int start-row) (inc (int end-row))))))

; Return a list of coord values from coord keys
; Ex: [b1, b2, b3] -> [5, 3, 19]
(defn get-coord-values [args all-values]
  (reduce (fn [acc el]
            (if (cs/includes? el ":")
              (into acc (unpack-range (cs/trim el) all-values))
              ; Else
              (if (> (count (str (get-coord-value all-values (cs/trim el)))) 0)
                (conj acc (js/parseFloat (get-coord-value all-values (cs/trim el))))
                acc))) [] args))

; Cells (args) that should update when a formula is updated
; Ex: e1: =sum(b3:b6) -> [b3 b4 b5 b6] 
; Formula map at key e1 will receive this vector so we know what cells to watch
(defn get-formula-dependency-cells [args]
  (reduce (fn [acc el]
            (if (cs/includes? el ":")
              (let [values (cs/split el #":")
                    letter (first (first values))
                    start-row (subs (first values) 1)
                    end-row (subs (last values) 1)]
                (into acc
                      (map #(str letter %)
                           (range (int start-row) (inc (int end-row))))))
              (conj acc (cs/trim el)
                    acc)))
          [] args))

; Run a formula with the args provided
(defn compute-formula [formula all-values update-formula-cell-map coord]
  (if (> (count formula) 3)
    (if-let [op (get operations (keyword (subs formula 1 4)))]
      (let [arg-start (inc (cs/index-of formula "("))
            arg-end (cs/index-of formula ")")]
        (when (and arg-start arg-end op)
          (let [args
                (cs/split (cs/trim (subs formula arg-start arg-end)) #",")]
            (update-formula-cell-map
             coord
             (get-formula-dependency-cells args))
            (.toFixed (op
                       (vec (get-coord-values args all-values))) 2)))))))
