(ns seven.components.spreadsheet
  (:require [reagent.core :as r]
            [seven.components.ui :refer [component-wrapper]]))

(def a-to-z (map char (range 97 123)))
(def numbers (vec (range 100)))

(defn main []
  [:div {:class "card"}
   [:div {:class "card-header"}
    [:div {:class "card-header-title"}
     "Cells"]]
   [:div.spreadsheet-container
    [:table
    ; A-Z titles
     [:thead
      [:tr
       [:th.spreadsheet-title-letter {:key "spacer"} ""]
       (map (fn [x] [:th.spreadsheet-title-letter {:key x} [:span x]]) a-to-z)]]
     [:tbody
      (map
       (fn [number]
         [:tr {:key (str "row-" "number-" number)}
         ; Row number title on side
          [:th.spreadsheet-title-number [:span  number]]
         ; Populate row [number] letter A B C D etc
          (map (fn [letter]
                 [:td {:key (str letter number)}
                 ; Each input corresponding to a letter
                  [:input.input
                   {:key (str "spreadsheet-input " letter number)
                    :class (str "spreadsheet-input " letter number)}]]) a-to-z)])
       numbers)]]]])
