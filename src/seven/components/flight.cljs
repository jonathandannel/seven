(ns seven.components.flight
  (:require [reagent.core :as r]
            [seven.components.ui :refer [component-wrapper]]
            [seven.components.helpers :as helpers]))

(def opts {:1 "One way flight" :2 "Round trip flight"})

(defonce state (r/atom
                {:active-option 1
                 :depart-date {:value ""
                               :errors {:format false :chars false :invalid false}}
                 :return-date {:value ""
                               :errors {:format false :chars false :invalid false}}}))

; Pointers to date vals, watch them for errors as they're updated
(def depart-cursor (r/cursor state [:depart-date :value]))
(def return-cursor (r/cursor state [:return-date :value]))

; Update error based on :depart-date or :return-date fields 
(defn update-error [field err bool]
  (swap! state assoc-in [(keyword field) :errors (keyword err)] bool))

; Set error fields true or false on input state change
(defn check-errors [field value]
  (let [char-err (helpers/bad-chars? value) format-err (helpers/bad-format? value)]
    (update-error field :chars char-err)
    (update-error field :format format-err)
    (if
     (not (or char-err format-err (< (count value) 1)))
      (cond
        (= field :depart-date)
        (update-error field :invalid (helpers/bad-start-date? value))
        (= field :return-date)
        (update-error field :invalid (helpers/bad-return-date? @depart-cursor value)))
      ; Remove the invalid state when date isn't fully entered again
      (update-error field :invalid false))))

; Pass changed state values to `check-errors`, %4th argument is new state after change
(add-watch depart-cursor :depart-watcher #(check-errors :depart-date %4))
(add-watch return-cursor :return-watcher #(check-errors :return-date %4))

; Input handlers
(defn handle-select [e]
  (let [v (-> e .-target .-value)]
    (swap! state assoc :active-option v)))

(defn handle-date-change [e]
  (let [k (-> e .-target .-name) v (-> e .-target .-value)]
    (cond
      (= k "depart") (reset! depart-cursor v)
      (= k "return") (reset! return-cursor v))))

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
      (and (-> @state :depart-date :errors :format) [:p {:class "help is-danger"} "Date must be in MM/DD/YYYY format"])
      (and (-> @state :depart-date :errors :chars) [:p {:class "help is-danger"} "Date may only contain numbers and slashes"])
      (and (-> @state :depart-date :errors :invalid) [:p {:class "help is-danger"} "Depart date must be in the future"])
      [:input {:class "input" :value (-> @state :depart-date :value) :on-change handle-date-change :name "depart" :type "text" :placeholder "ex: 02/31/2021"}]]]
    [:div {:class "field"}
     [:label {:class "label"} "Return"]
     [:div {:class "control"}
      (and (-> @state :return-date :errors :format) [:p {:class "help is-danger"} "Date must be in MM/DD/YYYY format"])
      (and (-> @state :return-date :errors :chars) [:p {:class "help is-danger"} "Date may only contain numbers and slashes"])
      (and (-> @state :return-date :errors :invalid) [:p {:class "help is-danger"} "Return date must be after depart date"])
      [:input {:class "input" :value (-> @state :return-date :value) :on-change handle-date-change :name "return" :disabled (= (@state :active-option) 1) :type "text" :placeholder "ex: 03/08/2021"}]]]]])
