(ns seven.components.crud
  (:require [reagent.core :as r]
            [seven.components.ui :refer [component-wrapper]]))

(defonce name-list (r/atom [{:first-name "Jonathan" :last-name "Dannel"}]))
(defonce filter-query (r/atom ""))
(defonce active-name (r/atom {:first-name "Jonathan" :last-name "Dannel"}))

(defn can-create? []
  (not (some #(= % @active-name) name-list)))

(defn create-entry []
  (let [first-name (@active-name :first-name)
        last-name (@active-name :last-name)]
    (if (and
         (count first-name)
         (count last-name)
         (can-create?))
      (swap! name-list conj {:first-name first-name :last-name last-name}))))

(defn handle-filter-change [e]
  (reset! filter-query (-> e .-target .-value)))

(add-watch name-list :name-state #(print %4))
(add-watch filter-query :query-state #(print %4))
(add-watch active-name :active-state #(print %4))

(defn main []
  [component-wrapper "CRUD"
   [:div.rows
    [:div.field.is-flex.is-flex-row.mb-5
     [:div.level
      [:div.level-left.mr-3
       [:label.label "Filter "]]
      [:div.level-right
       [:div.control
        [:input.input.is-primary {:on-change handle-filter-change}]]]]]
    [:div.columns
     ; List
     [:div.column.is-half
      [:div.menu
       [:ul.menu-list.pr-2 {:style {:list-style-type "none" :margin 0}}
        (doall
         (for [person @name-list]
           (let [first-name (person :first-name)
                 last-name (person :last-name)]
             ^{:key (str first-name last-name)}
             [:li {:on-click #(reset! active-name person)}
              [:a {:class (if (= @active-name person) "is-active")}
               first-name " " last-name]])))]]]
      ; Right side edit
     [:div.column.is-half
      [:div.columns
       [:div.column.is-one-quarter.mr-4
        [:div.is-flex.is-flex-direction-column
         [:label.label.pt-2 "Name"]
         [:label.label {:style {:padding-top "0.7em"}} "Surname"]]]
       [:div.column.auto
        [:div.is-flex.is-flex-direction-column
         [:input.input.mb-2
          {:on-change #(swap! active-name assoc :first-name (-> % .-target .-value))
           :value (@active-name :first-name)}]
         [:input.input
          {:on-change #(swap! active-name assoc :last-name (-> % .-target .-value))
           :value (@active-name :last-name)}]]]]]]
    ; Bottom buttons
    [:div.is-flex
     [:button.button.is-primary.mr-3 {:on-click create-entry} "Create"]
     [:button.button.is-primary.mr-3 "Update"]
     [:button.button.is-danger.mr-3 "Delete"]]]])
