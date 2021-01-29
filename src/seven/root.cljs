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

(def components [["Counter" counter/main]
                 ["Temperature" temperature/main]
                 ["Flight Booker" flight/main]
                 ["Timer" timer/main]
                 ["CRUD" crud/main]
                 ["Circles" circles/main]
                 ["Spreadsheet" spreadsheet/main]])

(def active-tab (r/atom 0))

(defn change-tab [index]
  (reset! active-tab index))

(defn root []
  (let [active-tab @active-tab]
    [:div.container {:style {:max-width 700}}
     [:div.tabs.is-toggle.is-boxed.is-centered.is-full-width
      [:ul
       (map-indexed
        (fn [index [component-name]]
          [:li {:key (str "component-" index) :class (if (= index active-tab) "is-active" "") :on-click #(change-tab index)}
           [:a component-name]]) components)]]
     ((last (get components active-tab)))]))
