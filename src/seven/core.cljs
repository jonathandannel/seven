(ns seven.core
  (:require
   [reagent.dom :as d]
   [seven.root :refer [root]]))

;; Mount and render our root component to #app div
(defn mount-root []
  (d/render [root] (.getElementById js/document "app")))

;; Runs on start
(defn init! []
  (mount-root))
