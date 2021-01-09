(ns seven.components.temperature
  (:require [reagent.core :as r]
            [seven.components.ui :refer [component-wrapper]]))

(defonce state (r/atom {:c 0 :f 32}))

(defn to-f [n]
  (Math.round (+ (* n (/ 9 5)) 32)))

(defn to-c [n]
  (Math.round (* (- n 32) (/ 5 9))))

(defn handle-change [e]
  ; Input name is either "f" or "c", set that key's value and convert the other
  (let [k (-> e .-target .-name) v (-> e .-target .-value)]
    (cond
      ; Nil state on empty input, show placeholder
      (= v "") (reset! state {:f nil :c nil})
      (= k "f") (reset! state {:f v :c (to-c v)})
      (= k "c") (reset! state {:c v :f (to-f v)}))))

; Easily replace input value without needing to delete/backspace
(defn select-all [e]
  (.select (.-target e)))

(defn temp-input [t]
  [:input {:type "number" :name t :value ((keyword t) @state) :on-focus select-all :on-change handle-change :placeholder t}])

(defn main []
  [component-wrapper "Temperature converter"
   [:div
    [temp-input "c"]
    [temp-input "f"]]])
