(ns seven.components.temperature
  (:require [reagent.core :as r]
            [seven.components.ui :refer [component-wrapper]]))

(defonce state (r/atom {:c 0 :f 32}))

(defn to-f [n]
  (Math.round (+ (* n (/ 9 5)) 32)))

(defn to-c [n]
  (Math.round (* (- n 32) (/ 5 9))))

(defn handle-change [e]
  ; Input name is either "f" or "c" 
  ; Set that state map key's value and convert the other
  (let [k (-> e .-target .-name) v (-> e .-target .-value)]
    (cond
      ; Nil state on empty input value, show placeholder
      (= v "") (reset! state {:f nil :c nil})
      (= k "f") (reset! state {:f v :c (to-c v)})
      (= k "c") (reset! state {:c v :f (to-f v)}))))

; Easily replace input value without needing to delete/backspace
(defn select-all [e]
  (.select (.-target e)))

(defn temp-input [t]
  [:input {:type "number"
           :class "input is-primary"
           :name t
           :placeholder (if (= t "f") "Fahrenheit" "Celsius")
           :value ((keyword t) @state)
           :on-focus select-all
           :on-change handle-change}])

(defn main []
  [component-wrapper "Temperature converter"
   [:div {:class "columns"}
    [:div {:class "column is-half"}
     [:h2 "Celsius"]
     [temp-input "c"]]
    [:div {:class "column is-half"}
     [:h2 "Fahrenheit"]
     [temp-input "f"]]]])
