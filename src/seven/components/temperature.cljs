(ns seven.components.temperature
  (:require [reagent.core :as r]
            [seven.components.ui :refer [component-wrapper]]))

(defonce state (r/atom {:c 0 :f 32}))

(defn to-f [n]
  (Math.round (+ (* n (/ 9 5)) 32)))

(defn to-c [n]
  (Math.round (* (- n 32) (/ 5 9))))

(defn handle-change [e]
  ; Set temp k/v pair and convert the other
  (let [k (-> e .-target .-name)
        v (-> e .-target .-value)]
    (cond
      (= v "")
      (reset! state {:f nil :c nil})
      (= k "f")
      (reset! state {:f v :c (to-c v)})
      (= k "c")
      (reset! state {:c v :f (to-f v)}))))

; Easily replace input value without needing to delete/backspace
(defn select-all [e]
  (.select (.-target e)))

(defn temp-input [t]
  [:div.field
   [:label.label (if (= t "f") "Fahrenheit" "Celsius")]
   [:div.control
    [:input.input.is-primary {:type "number"
                              :name t
                              :placeholder (str "°" t)
                              :value ((keyword t) @state)
                              :on-focus select-all
                              :on-change handle-change}]]])

(defn main []
  [component-wrapper "Temperature converter"
   [:div.columns
    [:div.column.is-half
     [temp-input "c"]]
    [:div.column.is-half
     [temp-input "f"]]]])
