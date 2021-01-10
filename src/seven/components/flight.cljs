(ns seven.components.flight
  (:require [reagent.core :as r]
            [seven.components.ui :refer [component-wrapper]]
            [clojure.string :as s]))

(def opts {:1 "One way flight" :2 "Round trip flight"})

(defonce state (r/atom {:active-option 1 :errors [2 5] :depart-date "" :return-date ""}))

(defn handle-select [e]
  (let [v (-> e .-target .-value)]
    (swap! state assoc :active-option v)))

(defn handle-date-change [e]
  (let [k (-> e .-target .-name) v (-> e .-target .-value)]
    (swap! state assoc (keyword k)  v)))

(defn parse-date [v] 
    (let [dmy (s/split v #"/")]
      
      
      
      ))
    

(defn 

;4th argument of callback is new value
(add-watch state :validator
  (fn [_ _ _ a] 
    (print a)))
  




(defn main []
  [component-wrapper "Flight booker"
   [:div {:class "content"}
    [:div {:class "field"}
     [:div {:class "control"}
      [:div {:class "select is-primary"}
       [:select {:value (@state :active-option)
                 :on-change handle-select}
        [:option {:value 1} (opts :1)]
        [:option {:value 2} (opts :2)]]]]]
    [:div {:class "field"}
     [:label {:class "label"} "Depart"]
     [:div {:class "control"}
      [:input {:class "input" :value (@state :depart-date) :on-change handle-date-change :name "depart-date" :type "text" :placeholder "ex: 02/31/2021"}]]]
    [:div {:class "field"}
     [:label {:class "label"} "Return"]
     [:div {:class "control"}
      [:input {:class "input" :value (@state :return-date) :on-change handle-date-change :name "return-date"  :disabled (seq (:errors @state)) :type "text" :placeholder "ex: 03/08/2021"}]]]]])
