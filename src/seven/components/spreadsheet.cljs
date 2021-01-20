(ns seven.components.spreadsheet
  (:require [reagent.core :as r]
            [seven.components.ui :refer [component-wrapper]]))

(def a-to-z (conj (map char (range 97 123)) nil))

(defn main []
  [component-wrapper "Cells"
   [:table {:style {:overflow "scroll"}}
    [:thead
     [:tr
      (map (fn [x] [:th {:key x} x]) a-to-z)]]
    [:tbody
     (map (fn [x] [:tr {:key x} [:th x]]) (range 26))]]])
