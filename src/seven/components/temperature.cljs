(ns seven.components.temperature
  (:require [reagent.core :as r]
            [seven.components.ui :as ui]))

(enable-console-print!)

(defonce state (r/atom {:c nil :f nil}))

(add-watch state :watcher
           (fn [_key _atom old-state new-state]
             (print old-state)
             (print new-state)))

(defn handle-change [e]
  (let [_name (-> e .-target .-name) _val (-> e .-target .-value)]
    (swap! state assoc-in [(keyword _name)] _val)))

(defn main []
  [ui/component-wrapper "Temperature converter"
   [:div
    [:h4 "Celsius"]
    [:input {:type "number" :name "c" :value (:c @state) :on-change handle-change :placeholder "celsius"}]
    [:h4 "Fahrenheit"]
    [:input {:type "number" :name "f" :value (:f @state) :on-change handle-change :placeholder "fahrenheit"}]]])
