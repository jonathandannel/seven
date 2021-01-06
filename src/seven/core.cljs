(ns seven.core
  (:require
   [reagent.dom :as d]
   [seven.root :as s]))

(defn mount-root []
  (d/render [s/root] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
