(ns seven.components.crud
  (:require [reagent.core :as r]
            [clojure.string :refer [lower-case]]
            [seven.components.ui :refer [component-wrapper]]))

(defonce name-list (r/atom
                    [{:first-name "Jonathan" :last-name "Dannel"}
                     {:first-name "Bardia" :last-name "Pourvakil"}
                     {:first-name "Musashi" :last-name "Miyamoto"}]))

(defonce filter-query (r/atom ""))
(defonce active-name (r/atom {:first-name "Jonathan" :last-name "Dannel"}))
(defonce updating (r/atom {:first-name "Jonathan" :last-name "Dannel"}))

(defn select-name [person]
  (reset! active-name person)
  (reset! updating person))

(defn can-create? [n]
  (not (boolean (some #(= n %) @name-list))))

(defn create-entry []
  (let [first-name (@active-name :first-name)
        last-name (@active-name :last-name)]
    (if (and
         (not= first-name "")
         (not= last-name "")
         (can-create? @active-name))
      (swap! name-list conj {:first-name first-name :last-name last-name}))))

(defn update-entry []
  (let [index (.indexOf @name-list @updating)]
    (if (can-create? @active-name)
      (swap! name-list assoc index @active-name))
    (select-name @active-name)))

(defn delete-entry []
  (let [filtered  (filterv #(not= % @active-name) @name-list)]
    (reset! name-list filtered)
    (reset! active-name {:first-name "" :last-name ""})))

(defn filter-entry [curr]
  (let [query-length (count @filter-query)]
    (if (=
         (subs (lower-case (get curr :last-name)) 0 query-length)
         (lower-case @filter-query))
      true)))

(defn handle-filter-change [e]
  (reset! filter-query (-> e .-target .-value))
  (let [filtered (filterv #(filter-entry %) @name-list)]
    (if (> (count filtered) 0) (select-name (first filtered)))))

(defn main []
  [component-wrapper "CRUD"
   [:div.rows
    [:div.column-is-half
     [:div.field.is-flex.is-flex-row.mb-5
      [:div.level
       [:div.level-left.mr-3
        [:label.label "Filter "]]
       [:div.level-right
        [:div.control
         [:input.input.is-primary {:on-change handle-filter-change}]]]]]]
    [:div.columns
     ; List
     [:div.column.is-half
      [:div.menu {:style {:overflow-y "scroll" :height "15vh"}}
       [:ul.menu-list.pr-2 {:style {:list-style-type "none" :margin 0}}
        (doall
         (for [person (filterv #(filter-entry %) @name-list)]
           (let [first-name (person :first-name)
                 last-name (person :last-name)]
             ^{:key (str first-name last-name)}
             [:li {:on-click #(select-name person)}
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
     [:button.button.is-primary.mr-3  {:disabled (not (can-create? @active-name)) :on-click create-entry} "Create"]
     [:button.button.is-primary.mr-3 {:on-click update-entry} "Update"]
     [:button.button.is-danger.mr-3 {:on-click delete-entry} "Delete"]]]])
