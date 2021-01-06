(ns seven.root
  (:require [seven.components :as c]))

(defn root []
  [:div
   [:h1 "seven.root components:"]
   [c/counter]
   [c/temperature]])
