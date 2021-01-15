(ns seven.components.crud
  (:require [reagent.core :as r]
            [seven.components.ui :refer [component-wrapper]]))
(defn main []
  [component-wrapper "CRUD"
   [:div.rows
    [:div.field.is-flex.is-flex-row.mb-5
     [:div.level
      [:div.level-left.mr-3
       [:label.label "Filter "]]
      [:div.level-right
       [:div.control
        [:input.input.is-primary {:placeholder "Search"}]]]]]
    [:div.columns
     ; List
     [:div.column.is-half
      [:div.menu
       [:ul.menu-list.pr-2 {:style {:list-style-type "none" :margin 0}}
        [:li
         [:a "Jonathan Dannel"]]
        [:li
         [:a "Bardia Pourvakil"]]
        [:li
         [:a.is-active "Yuhui Shi"]]
        [:li
         [:a "Tim Heidecker"]]]]]
      ; Right side edit
     [:div.column.is-half
      [:div.columns
       [:div.column.is-one-quarter.mr-4
        [:div.is-flex.is-flex-direction-column
         [:label.label.pt-2 "Name"]
         [:label.label {:style {:padding-top "0.7em"}} "Surname"]]]
       [:div.column.auto
        [:div.is-flex.is-flex-direction-column
         [:input.input.mb-2]
         [:input.input]]]]]]
    ; Bottom buttons
    [:div.is-flex
     [:button.button.is-primary.mr-3 "Create"]
     [:button.button.is-primary.mr-3 "Update"]
     [:button.button.is-danger.mr-3 "Delete"]]]])
