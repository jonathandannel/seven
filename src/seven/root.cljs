(ns seven.root
  (:require
   [reagent.core :as r]
   [seven.components.counter :as counter]
   [seven.components.temperature :as temperature]
   [seven.components.flight :as flight]
   [seven.components.timer :as timer]
   [seven.components.crud :as crud]
   [seven.components.circles :as circles]
   [seven.components.spreadsheet :as spreadsheet]))

(def components [{:name "Counter" :component counter/main}
                 {:name "Temperature" :component temperature/main}
                 {:name "Flight Booker" :component flight/main}
                 {:name "Timer" :component timer/main}
                 {:name "Crud" :component crud/main}
                 {:name "Circles" :component circles/main}
                 {:name "Spreadsheet" :component spreadsheet/main}])

(def active-tab (r/atom 0))

(defn root []
  [:div.container.root-container
   [:div.tabs.is-toggle.is-boxed.is-centered.is-full-width.mb-5
    [:ul
     (doall
      (map-indexed
       (fn [index {component-name :name}]
         [:li
          {:key (str "component-" index)
           :class (when (= index @active-tab) "is-active")
           :on-click #(reset! active-tab index)}
          [:a component-name]]) components))]]
   (((get components @active-tab) :component))])
