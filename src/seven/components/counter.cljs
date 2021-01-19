(ns seven.components.counter
  (:require [reagent.core :as r]
            [seven.components.ui :refer [component-wrapper]]))

(def total (r/atom 0))

(defn main []
  [component-wrapper "Counter"
   [:div.level
    [:span.level-left.is-size-5 "The button has been clicked " @total " times"]
    [:div.level-right
     [:button.button.is-primary  {:on-click #(swap! total inc)}
      "Click to increment"]]]])
