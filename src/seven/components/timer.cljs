(ns seven.components.timer
  (:require [reagent.core :as r]
            [seven.components.ui :refer [component-wrapper]]))

(defonce state (r/atom {:elapsed-duration 0 :chosen-duration 15 :max-duration 32}))
(defonce interval-object (r/atom nil))
(defonce timer-running (r/atom false))

(defn handle-duration-change [e]
  (let [new-duration (-> e .-target .-value)]
    (swap! state assoc [:chosen-duration] new-duration)
    (print new-duration)))

; Assign the interval object to an atom so we can clear it when we need to
(defn set-interval []
  ; Clear interval if it exists to avoid weird behavior
  (if (not= @interval-object nil) (js/clearInterval @interval-object))
  (reset! interval-object
          (js/setInterval
           ; Add a second to elapsed duration on every tick
           #(swap! state update-in [:elapsed-duration] inc) 1000)))

(defn reset-timer []
  ; Clear accrued time and set a new interval
  (reset! (state :elapsed-duration) 0)
  (set-interval))

(add-watch state :state-watcher
           (fn [k a o n] (print n)))

; Start timer on mount
(set-interval)

(defn main []
  [component-wrapper "Timer"
   ; Auto run on mount
   [:div {:class "content" :style {:height "fit-content"}}
    [:progress {:class "progress is-primary" :value "33" :max "66"}]
    [:div {:class "block"}]
    [:span (@state :elapsed-duration) " seconds elapsed"]
    [:div {:class "block"}]
    [:input {:type "range"
             :on-change handle-duration-change
             :style {:width "100%"}
             :step 1
             :value (@state [:chosen-duration])
             :min 0
             :max (@state :max-duration)}]

    [:div {:class "block"}]
    [:span (@state :chosen-duration) " seconds maximum"]
    [:div {:class "block"}]
    [:button {:class "button is-primary" :on-click set-interval} "Reset"]]])
