(ns seven.components.counter
  (:require [reagent.core :as r]
            [seven.components.ui :as ui]))

(defn main []
  (let [total (r/atom 0)]
    (fn []
      [ui/component-wrapper "Counter"
       [:div {:class "level"}
        [:h4 {:class "level-left"} "Click count: " @total]
        [:button {:class "button is-danger level-right"
                  :on-click #(swap! total inc)}
         "Increment counter"]]])))
