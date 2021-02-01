(ns seven.components.timer
  (:require [reagent.core :as r]
            [seven.components.ui :refer [component-wrapper]]))

(defonce state (r/atom {:elapsed-duration 0 :chosen-duration 60 :max-duration 90}))
(defonce ticker (r/atom nil))

(defn handle-duration-change [e]
  (let [new-duration (-> e .-target .-value)]
    (swap! state assoc :chosen-duration new-duration)))

; Assign the js interval object to an atom, inc state on every tick
(defn set-interval []
  ; Clear interval if it exists to avoid weird behavior
  (if @ticker (js/clearInterval @ticker))
  (reset! ticker
          (js/setInterval
           ; Inc 1 second to elapsed duration on every tick
           #(swap! state update :elapsed-duration inc) 1000)))

(defn reset-timer []
  (swap! state assoc :elapsed-duration 0)
  (set-interval))

; Turn seconds into human readable minutes (0.5 -> 0:30)
(defn format-sec [s]
  (let [decimal (/ s 60)
        minutes (js/Math.floor (/ s 60))]
    (let [seconds (js/Math.round (* (- decimal minutes) 60))]
      (str minutes ":" (if (< seconds 10) (str "0" seconds) seconds)))))

; Stop the clock if we've reached the chosen duration
(add-watch state :seconds-watcher
           #(when
             (>= (@state :elapsed-duration) (@state :chosen-duration))
              (js/clearInterval @ticker)))

; Start timer on mount
(set-interval)

(defn main []
  [component-wrapper "Timer"
   [:div.content
    [:progress.progress.is-primary
     {:value (str (@state :elapsed-duration))
      :max (str (@state :chosen-duration))}]
    [:div.is-flex.is-justify-content-center
     [:h5.subtitle.is-4.mr-2
      {:class (when
               (>= (@state :elapsed-duration) (@state :chosen-duration))
                " has-text-danger")}
      (format-sec (@state :elapsed-duration))]]
    [:input.fullwidth
     {:type "range"
      :on-change handle-duration-change
      :disabled
      (>= (@state :elapsed-duration) (@state :chosen-duration))
      :step 1
      :min 0
      :max (@state :max-duration)}]
    [:div.block]
    [:div.level
     [:div.level-left
      [:span.tag.is-info.is-medium.p-4.mt-1
       [:span.icon.mr-1.pb-1
        [:i.fas.fa-info]]
       "Timer set for "
       (format-sec (@state :chosen-duration))]]
     [:div.level-right
      [:button.button.is-primary
       {:on-click reset-timer}
       "Reset"]]]]])
