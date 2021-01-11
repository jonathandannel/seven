(ns seven.components.flight
  (:require [reagent.core :as r]
            [seven.components.ui :refer [component-wrapper]]
            [seven.components.helpers :as helpers]))

(def opts {:1 "One way flight" :2 "Round trip flight"})

(defonce state (r/atom {:active-option 1
                        :depart-date {:value "" :errors #{}}
                        :return-date {:value "" :errors #{}}}))

(defn add-error [k err]
  (swap! state update-in [(keyword k) :errors] conj err))

(def depart-cursor (r/cursor state [:depart-date]))
(def return-cursor (r/cursor state [:return-date]))

(add-watch depart-cursor :depart-watcher
           (fn [k a o n]
             (if (< (count (helpers/parse-date (n :value))) 8)
               (add-error :depart-date "Date must be MM-DD-YYYY"))
             (if (re-find #"[a-zA-Z]" (n :value))
               (add-error :depart-date "Only numbers and slashes allowed"))))

(add-watch state :state-watcher #(-> %4 print))

(defn handle-select [e]
  (let [v (-> e .-target .-value)]
    (swap! state assoc :active-option v)))

(defn handle-date-change [e]
  (let [k (-> e .-target .-name) v (-> e .-target .-value)]
    (cond
      (= k "depart") (swap! depart-cursor assoc :value v)
      (= k "return") (swap! return-cursor assoc :value v))))

(defn get-now-date []
  (java.util.Date.))

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
      [:input {:class "input" :value (-> @state :depart-date :value) :on-change handle-date-change :name "depart" :type "text" :placeholder "ex: 02/31/2021"}]]]
    [:div {:class "field"}
     [:label {:class "label"} "Return"]
     [:div {:class "control"}
      [:input {:class "input" :value (-> @state :return-date :value) :on-change handle-date-change :name "return" :disabled (= (@state :active-option) 1) :type "text" :placeholder "ex: 03/08/2021"}]]]]])
