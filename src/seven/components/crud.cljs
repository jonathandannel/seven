(ns seven.components.temperature
  (:require [reagent.core :as r]
            [seven.components.ui :refer [component-wrapper]]))
(defn main []
  [component-wrapper "Temperature converter"
   [:div.columns
    [:div.column.is-half]
    [:div.column.is-half]]])
