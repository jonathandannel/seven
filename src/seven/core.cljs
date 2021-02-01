(ns seven.core
  (:require
   [reagent.dom :as d]
   [seven.root :refer [root]]))

(defn mount-root []
  (d/render [root] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
