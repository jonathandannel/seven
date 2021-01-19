(ns seven.components.counter
  (:require [reagent.core :as r]
            [seven.components.ui :refer [component-wrapper]]))

(def total (r/atom 0))

(defn main []
  [component-wrapper "Counter"
   [:div.level
    [:div.level-left
     [:div.level
      [:div.level-left
       [:span "You've clicked the button "]
       [:span.tag.is-medium.is-info.level-right.ml-1 @total]
       [:span.ml-1 " times."]]]]
    [:div.level-right
     [:button.button.is-primary  {:on-click #(swap! total inc)}
      "Increment"]]]])
