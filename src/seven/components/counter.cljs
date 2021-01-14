(ns seven.components.counter
  (:require [reagent.core :as r]
            [seven.components.ui :refer [component-wrapper]]))

(defn main []
  (let [total (r/atom 0)]
    (fn []
      [component-wrapper "Counter"
       [:div.level
        [:span.level-left.is-size-4 "Click count: " @total]
        [:button.button.is-danger-level-right  {:on-click #(swap! total inc)}
         "Click to increment"]]])))
