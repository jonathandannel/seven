(ns seven.components.spreadsheet.util
  (:require [clojure.string :as s]))

(def operations
  {:sum #(apply + %)
   :sub #(apply - %)
   :mul #(apply * %)
   :div #(apply / %)
   :avg #(/ (apply + %) (count %))})

(defn is-function [v]
  (= (first v) "="))

(defn get-coord-value [values string]
  (let [cell (values (keyword string))]
    (or (get cell :computed) (get cell :value))))

; Return a list of coords in string range
; Ex: "b1:b4" -> [b1, b2, b3, b4]
(defn unpack-range [_range all-values]
  (let [values (s/split _range #":")
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
(defn map-coords-to-values [args all-values]
  (reduce (fn [acc el]
            (if (s/includes? el ":")
              (into acc (unpack-range (s/trim el) all-values))
              ; Else
              (if (> (count (str (get-coord-value all-values (s/trim el)))) 0)
                (conj acc (js/parseFloat (get-coord-value all-values (s/trim el))))
                ; Return acc
                acc))) [] args))

; Cells (args) that should update when a formula is updated
(defn get-formula-cells [args]
  (reduce (fn [acc el]
            (if (s/includes? el ":")
              (let [values (s/split el #":")
                    letter (first (first values))
                    start-row (subs (first values) 1)
                    end-row (subs (last values) 1)]
                (into acc
                      (map #(str letter %)
                           (range (int start-row) (inc (int end-row))))))
              (conj acc (s/trim el)
                ; Return acc 
                    acc)))
          [] args))

; Run a formula with the args provided
(defn compute-formula [formula all-values update-formula-cell-map coord]
  (if (> (count formula) 3)
    (if-let [op (get operations (keyword (subs formula 1 4)))]
      (let [arg-start (inc (s/index-of formula "("))
            arg-end (s/index-of formula ")")]
        (if (and arg-start arg-end op)
          (let [args
                (s/split (s/trim (subs formula arg-start arg-end)) #",")]
            (update-formula-cell-map coord (get-formula-cells args))
            (.toFixed (op
                       (vec (map-coords-to-values args all-values))) 2)))))))
