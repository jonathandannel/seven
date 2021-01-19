(ns seven.components.circles
  (:require [reagent.core :as r]
            [seven.components.ui :refer [component-wrapper]]))

(defonce all-paths (r/atom []))
(defonce history (r/atom []))
(defonce current-history-index (r/atom 0))
(defonce selected-circle-index (r/atom nil))
(defonce chosen-radius (r/atom 25))
(defonce history-paused-at (r/atom nil))
(defonce drawing-disabled (r/atom false))
(defonce ctx-ref (r/atom nil))

(defn redraw-canvas [ctx]
  (.clearRect ctx 0 0 640 480)
  (doseq [[idx, [circle]] (map-indexed vector (get @history @current-history-index))]
    (.stroke ctx circle)
    (set! (.-fillStyle ctx) "lightgrey")
    (if (= idx @selected-circle-index) (.fill ctx circle))))

; Find existing objects under cursor
(defn get-cursor-path [e]
  (reset! selected-circle-index nil)
  (let [ctx (-> e .-target (.getContext "2d"))
        rect (.getBoundingClientRect e.target)
        x (- e.clientX rect.left)
        y (- e.clientY rect.top)]
    (doseq [[idx [circle]] (map-indexed vector (get @history @current-history-index))]
      (if (.isPointInPath ctx circle x y)
        (do
          (reset! selected-circle-index idx)
          (reset! drawing-disabled true)
          (reset! chosen-radius (last (@all-paths @selected-circle-index)))))
      (redraw-canvas ctx))))

(add-watch selected-circle-index :selected-circle-watcher #(reset! drawing-disabled (boolean %4)))

(defn draw-circle [e]
  (let [ctx (-> e .-target (.getContext "2d"))
        rect (.getBoundingClientRect e.target)
        x (- e.clientX rect.left)
        y (- e.clientY rect.top)
        r 25
        circle (js/Path2D.)]
    (reset! ctx-ref ctx)
    (.arc circle x y r 0 (* 2 Math.PI))
    (if (not @drawing-disabled)
      (do
    ; If you make changes after an undo, the current state becomes the latest
        (if (< @current-history-index (- (count @history) 1))
          (do
            (swap! history #(subvec % 0 (+ @current-history-index 1)))
            (reset! all-paths (get @history @current-history-index))))
        (swap! all-paths conj [circle x y r])
        (swap! history conj @all-paths)
        (reset! current-history-index (- (count @history) 1))
        (redraw-canvas ctx)))))

; Bookmark place in history so we can ignore resizing before save
(defn start-updating [e]
  (.preventDefault e)
  (.stopPropagation e)
  (if @selected-circle-index
    (reset! history-paused-at (count @history))))

(defn edit-circle [e]
  (let [new-val (-> e .-target .-value)
        [_ x y] (get @all-paths @selected-circle-index)
        circle (js/Path2D.)]
    (.arc circle x y new-val 0 (* 2 Math.PI))
    (reset! chosen-radius new-val)
    (if (< @current-history-index (- (count @history) 1))
      (do
        (swap! history #(subvec % 0 (+ @current-history-index 1)))
        (reset! all-paths (get @history @current-history-index))))
    (swap! all-paths assoc @selected-circle-index [circle x y new-val])
    (swap! history conj @all-paths)
    (reset! current-history-index (- (count @history) 1))
    (reset! chosen-radius new-val)
    (redraw-canvas @ctx-ref)))

(defn remove-erroneous-history []
  (let [old-history @history]
    (reset! history (conj (subvec old-history 0 @history-paused-at) (get @history @current-history-index)))
    (reset! current-history-index (- (count @history) 1))
    (reset! history-paused-at nil)))

(defn undo []
  (if (> @current-history-index -1)
    (swap! current-history-index dec))
  (redraw-canvas @ctx-ref))

(defn redo []
  (if (<=  (+ 1 @current-history-index) (- (count @history) 1))
    (swap! current-history-index inc))
  (redraw-canvas @ctx-ref))

(defn reset []
  (.clearRect @ctx-ref 0 0 640 480)
  (reset! all-paths [])
  (reset! history [])
  (reset! current-history-index 0))

(defn main []
  [component-wrapper "Circle drawer"
   [:div.is-flex.is-flex-direction-column {:style {:width "max-content"}}
    [:div.container
     [:button.button.is-primary.mr-5 {:on-click undo}
      [:span.icon
       [:i.fas.fa-undo]]
      [:div.block]
      "Undo"]
     [:button.button.is-primary.mr-5 {:on-click redo}
      [:span.icon
       [:i.fas.fa-redo]]
      [:div.block]
      "Redo"]
     [:button.button.is-danger {:on-click reset}
      [:span.icon
       [:i.fas.fa-trash]]
      [:div.block]
      "Reset"]]
    [:div.box.mt-5
     [:div.modal {:class (if @history-paused-at "is-active")}
      [:div.modal-background {:style {:background "transparent"} :on-click remove-erroneous-history}]
      [:div.modal-content
       [:div.panel.container.is-info {:style {:background "white" :width "50%"}}
        [:div.panel-heading.is-size-6 "Edit circle radius"]
        [:div.panel-block
         [:div.field.pt-2 {:style {:width "100%" :background "white"}}
          [:div.control
           [:div.container
            [:input {:style {:width "100%"} :on-change edit-circle :type "range" :value @chosen-radius :step 1 :min 10 :max 80}]]]]]]]]
     [:canvas {:on-context-menu (if (> (count @all-paths) 0) start-updating) :on-click draw-circle :on-mouse-move get-cursor-path  :width (* 640 0.8) :height (* 480 0.8)}]]]])
