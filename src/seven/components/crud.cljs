(ns seven.components.crud
  (:require [reagent.core :as r]
            [seven.components.ui :refer [component-wrapper]]))
(defn main []
  [component-wrapper "CRUD"
   [:div.columns
    [:div.column.is-half]
    [:div.column.is-half]]])
