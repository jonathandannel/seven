(ns seven.root
  ;; Import components
  (:require [seven.components.counter :as counter]))

;; Root contains all our components 
(defn root []
  [counter/main])
