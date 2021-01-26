(ns seven.root
  ;; Import components
  (:require [seven.components.counter :as counter]
            [seven.components.flight :as flight]
            [seven.components.timer :as timer]
            [seven.components.crud :as crud]
            [seven.components.circles :as circles]
            [seven.components.spreadsheet :as spreadsheet]
            [seven.components.temperature :as temperature]))

;; Root contains all our components 
(defn root []
  [:<>
   [counter/main]
   [:div {:class "block"}]
   [temperature/main]
   [:div {:class "block"}]
   [flight/main]
   [:div {:class "block"}]
   [timer/main]
   [:div {:class "block"}]
   [crud/main]
   [:div.block]
   [circles/main]
   [spreadsheet/main]
   [:div {:class "block"}]])
