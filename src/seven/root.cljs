(ns seven.root
  ;; Import components
  (:require [seven.components.counter :as counter]
            [seven.components.temperature :as temperature]))

;; Root contains all our components 
(defn root []
  [:div {:class "content"}
   [counter/main]
   [:div {:class "block"}]
   [temperature/main]])
