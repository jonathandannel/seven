(ns seven.components.flight
  (:require [reagent.core :as r]
            [seven.components.ui :refer [component-wrapper]]
            [seven.components.flight.util :as util]))

(def opts {:1 "One way flight" :2 "Round trip flight"})

(def state (r/atom
            {:active-option 1
             :depart-date {:value ""
                           :errors {:format false :chars false :invalid false}}
             :return-date {:value ""
                           :errors {:format false :chars false :invalid false}}}))

; Pointers to date vals, watch them for errors as they're updated
(def depart-state-value (r/cursor state [:depart-date :value]))
(def return-state-value (r/cursor state [:return-date :value]))

; Toggle error values in :depart-date or :return-date fields 
(defn set-field-error [field err bool]
  (swap! state assoc-in [(keyword field) :errors (keyword err)] bool))

; Set error fields true or false on input state change
(defn check-errors [field value]
  (let [char-err (util/bad-chars? value)
        format-err (util/bad-format? value)]
    (set-field-error field :chars char-err)
    (set-field-error field :format format-err)
    (if (not (or char-err format-err (< (count value) 1)))
      (cond
        (= field :depart-date)
        (set-field-error field :invalid (util/bad-start-date? value))
        (= field :return-date)
        (set-field-error field :invalid (util/bad-return-date? @depart-state-value value)))
      ; Remove the invalid error when date isn't fully entered again
      (set-field-error field :invalid false))))

; Pass state values to `check-errors` after change
(add-watch depart-state-value :depart-value-watcher
           #(check-errors :depart-date %4))
(add-watch return-state-value :return-value-watcher
           #(check-errors :return-date %4))

(defn field-has-errors? [field]
  (contains?
   (set (vals (-> @state (get (keyword field)) :errors)))
   true))

; Input handlers
(defn handle-select [e]
  (let [v (-> e .-target .-value)]
    (swap! state assoc-in [:active-option] v)))

(defn handle-date-change [e]
  (let [k (-> e .-target .-name)
        v (-> e .-target .-value)]
    (cond
      (= k "depart")
      (reset! depart-state-value v)
      (= k "return")
      (reset! return-state-value v))))

(defn render-errors [field]
  [:<>
   (and (-> @state (get (keyword field)) :errors :format)
        [:p.help.is-danger
         "Date must be in MM/DD/YYYY format"])
   (and (-> @state (get (keyword field)) :errors :chars)
        [:p.help.is-danger
         "Date may only contain numbers and slashes"])
   (and (-> @state (get (keyword field)) :errors :invalid)
        [:p.help.is-danger
         (if (= (keyword field) :depart-date)
           "Date must be in the future"
           "Return date must be after depart date")])])

; TODO: Create one generalized input component
(defn main []
  [component-wrapper "Flight booker"
   [:div.content
    [:div.field
     [:div.control
      [:div.select.is-primary
       [:select {:value (@state :active-option)
                 :on-change handle-select}
        [:option {:value 1} (opts :1)]
        [:option {:value 2} (opts :2)]]]]]
    [:div.field
     [:label.label "Depart"]
     [:div.control
      [render-errors :depart-date]
      [:input.input
       {:class
        (when (field-has-errors? :depart-date)
          " is-danger")
        :value (-> @state :depart-date :value)
        :key "depart-input"
        :on-change handle-date-change
        :name "depart"
        :type "text"
        :placeholder "ex: 02/31/2021"}]]]
    [:div.field
     [:label.label "Return"]
     [:div.control
      [render-errors :return-date]
      [:input.input
       {:class (when (field-has-errors? :return-date) " is-danger")
        :key "return-input"
        :value (-> @state :return-date :value)
        :on-change handle-date-change
        :name "return"
        :disabled (= (int (@state :active-option)) 1)
        :type "text" :placeholder "ex: 03/08/2021"}]]]
    [:div.block]
    [:div.field
     [:div.control
      [:button.button.is-primary
       {:on-click
        #(js/alert
          (str "Thank you for booking with us!"
               "\n \n"
               "Depart date: " @depart-state-value "\n"
               "Return date: "
               (cond
                 (= (int (@state :active-option)) 1) "No return flight."
                 :else @return-state-value)))
        :disabled
        (or
         (= (count @depart-state-value) 0)
         (and
          (= (count @return-state-value) 0)
          (= @state :active-option 2))
         (or
          (field-has-errors? :depart-date)
          (field-has-errors? :return-date)))}
       "Book flight"]]]]])
