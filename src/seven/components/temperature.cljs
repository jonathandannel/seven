(ns seven.components.temperature
  (:require [reagent.core :as r]
            [seven.components.ui :refer [component-wrapper]]))

(def default-state
  {:c nil :f nil})

(defonce state (r/atom default-state))

(defn to-f [n]
  (+ (* n (/ 9 5)) 32))

(defn to-c [n]
  (* (- n 32) (/ 5 9)))

(defn handle-change [e]
  (let [k (-> e .-target .-name) v (-> e .-target .-value)]
    (cond
      (= v "") (reset! state default-state)
      (= k "f") (reset! state {:f v :c (to-c v)})
      (= k "c") (reset! state {:c v :f (to-f v)}))))

(defn main []
  [component-wrapper "Temperature converter"
   [:div
    [:h4 "Celsius"]
    [:input {:type "number" :name "c" :value (:c @state) :on-change handle-change :placeholder "celsius"}]
    [:h4 "Fahrenheit"]
    [:input {:type "number" :name "f" :value (:f @state) :on-change handle-change :placeholder "fahrenheit"}]]])
