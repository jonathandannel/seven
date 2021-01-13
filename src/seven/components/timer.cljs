(ns seven.components.timer
  (:require [reagent.core :as r]
            [seven.components.ui :refer [component-wrapper]]))

(defonce state (r/atom {:elapsed-duration 0 :chosen-duration 60 :max-duration 180}))
(defonce interval-object (r/atom nil))

(defn handle-duration-change [e]
  (let [new-duration (-> e .-target .-value)]
    (swap! state assoc :chosen-duration new-duration)))

; Assign the js interval object to an atom so we can clear it when we need to
(defn set-interval []
  ; Clear interval if it exists to avoid weird behavior
  (if interval-object (js/clearInterval @interval-object))
  (reset! interval-object
          (js/setInterval
           ; Inc 1 second to elapsed duration on every tick
           #(swap! state update :elapsed-duration inc) 1000)))

(defn reset-timer []
  ; Clear accrued time and set a new interval
  (swap! state assoc :elapsed-duration 0)
  (set-interval))

; Start timer on mount
(set-interval)

(add-watch state :seconds-watcher
           #(if (>= (@state :elapsed-duration) (@state :chosen-duration))
              (js/clearInterval @interval-object)))

(defn main []
  [component-wrapper "Timer"
   [:div {:class "content" :style {:height "fit-content"}}
    [:progress {:class "progress is-primary" :value (str (@state :elapsed-duration)) :max (str (@state :chosen-duration))}]
    [:div {:class "block"}]
    [:span {:class "is-size-5"} (@state :elapsed-duration) " seconds elapsed"
     (if (>= (@state :elapsed-duration) (@state :chosen-duration)) " - Done!")]
    [:div {:class "block"}]
    [:input {:type "range"
             :on-change handle-duration-change
             :style {:width "100%"}
             :step 1
             :min 0
             :max (@state :max-duration)}]

    [:div {:class "block"}]
    [:span {:class "is-size-5"} "Timer set for " (@state :chosen-duration) " seconds"]
    [:div {:class "block"}]
    [:button {:class "button is-primary" :on-click reset-timer} "Reset"]]])
