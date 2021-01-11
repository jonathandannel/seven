(ns seven.components.flight
  (:require [reagent.core :as r]
            [seven.components.ui :refer [component-wrapper]]
            [seven.components.helpers :as helpers]))

(def opts {:1 "One way flight" :2 "Round trip flight"})

; State 
(defonce state (r/atom
                {:active-option 1
                 :depart-date {:value ""
                               :errors {:format false :letters false}}
                 :return-date {:value ""
                               :errors {:format false :letters false :invalid false}}}))

; Pointers to date vals, watch them for errors as they're updated
(def depart-cursor (r/cursor state [:depart-date :value]))
(def return-cursor (r/cursor state [:return-date :value]))

(defn update-error [k err bool]
  (swap! state assoc-in [(keyword k) :errors (keyword err)] bool))

(defn check-errors [field value]
  (if (helpers/bad-format? value) (update-error field :format true) (update-error field :format false))
  (if (helpers/bad-format? value) (update-error field :format true) (update-error field :format false))
  (if (helpers/bad-format? value) (update-error field :format true) (update-error field :format false)))

(add-watch depart-cursor :depart-watcher
           (fn [k a o n]
             (check-errors :depart-date n)))
             ;(if (< (count (helpers/parse-date n)) 8)
               ;(update-error :depart-date :format true)
               ;(update-error :depart-date :format false))
             ;(if (re-find #"[a-zA-Z]" n)
               ;(update-error :depart-date :letters true)
               ;(update-error :depart-date :letters false))))

(add-watch state :state-watcher #(-> %4 print))

; Handlers
(defn handle-select [e]
  (let [v (-> e .-target .-value)]
    (swap! state assoc :active-option v)))

(defn handle-date-change [e]
  (let [k (-> e .-target .-name) v (-> e .-target .-value)]
    (cond
      (= k "depart") (reset! depart-cursor v)
      (= k "return") (reset! return-cursor v))))

; Component
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
