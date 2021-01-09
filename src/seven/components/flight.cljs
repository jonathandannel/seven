(ns seven.components.flight
  (:require [reagent.core :as r]
            [seven.components.ui :refer [component-wrapper]]))

(defonce state (r/atom {:c 0 :f 32}))

(defn main []
  [component-wrapper "Flight booker"
   [:div]])
