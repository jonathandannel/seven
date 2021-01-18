(ns seven.components.circles
  (:require [reagent.core :as r]
            [seven.components.ui :refer [component-wrapper]]))

(defonce all-paths (r/atom []))
(defonce selected-circle-index (r/atom nil))

(defonce history (r/atom []))
(defonce current-history-index (r/atom 0))

(defonce ctx-ref (r/atom nil))
(defonce chosen-radius (r/atom 25))
(defonce updating (r/atom false))
(defonce drawing-disabled (r/atom false))

(defn redraw-canvas [ctx]
  (.clearRect ctx 0 0 640 480)
  (doseq [[idx, [circle]] (map-indexed vector (get @history @current-history-index))]
    (.stroke ctx circle)
    (set! (.-fillStyle ctx) "lightgrey")
    (if (= idx @selected-circle-index) (.fill ctx circle))))

(defn get-cursor-path [e]
  (let [ctx (-> e .-target (.getContext "2d"))
        rect (.getBoundingClientRect e.target)
        x (- e.clientX rect.left)
        y (- e.clientY rect.top)]
    (doseq [[idx [circle]] (map-indexed vector (get @history @current-history-index))]
      (if (.isPointInPath ctx circle x y)
        (do
          (reset! selected-circle-index idx)
          (reset! drawing-disabled true))
        (reset! drawing-disabled false)))
    (redraw-canvas ctx)))

(defn on-mouse-down [e]
  (.preventDefault e)
  (let [ctx (-> e .-target (.getContext "2d"))
        rect (.getBoundingClientRect e.target)
        x (- e.clientX rect.left)
        y (- e.clientY rect.top)
        circle (js/Path2D.)]
    (reset! ctx-ref ctx)
    (.arc circle x y 25 0 (* 2 Math.PI))
    (if (not @drawing-disabled)
      (do
    ; If you make changes after an undo, then slice history up until this change
        (if (< @current-history-index (- (count @history) 1))
          (do
            (swap! history #(subvec % 0 (+ @current-history-index 1)))
            (reset! all-paths (get @history @current-history-index))))
        (swap! all-paths conj [circle x y])
        (swap! history conj @all-paths)
        (reset! current-history-index (- (count @history) 1))
        (redraw-canvas ctx)))))

(defn edit-circle [e]
  (reset! updating (count @history)))

(defn change-diameter [e]
  (let [new-val (-> e .-target .-value)
        [_ x y] (get @all-paths @selected-circle-index)
        circle (js/Path2D.)]
    (.arc circle x y new-val 0 (* 2 Math.PI))
    (reset! chosen-radius new-val)
    (if (< @current-history-index (- (count @history) 1))
      (do
        (swap! history #(subvec % 0 (+ @current-history-index 1)))
        (reset! all-paths (get @history @current-history-index))))
    (swap! all-paths assoc @selected-circle-index [circle x y])
    (swap! history conj @all-paths)
    (reset! current-history-index (- (count @history) 1))
    (redraw-canvas @ctx-ref)))

(defn remove-erroneous-history []
  (let [old-history @history]
    (reset! history (conj (subvec old-history 0 @updating) (get @history @current-history-index)))
    (reset! current-history-index (- (count @history) 1))
    (reset! updating nil)))

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

(defn handle-click [e]
  (if (= e.nativeEvent.which 1) (on-mouse-down e) (edit-circle e)))

(defn main []
  [component-wrapper "Circle drawer"
   [:div.is-flex.is-flex-direction-column
    [:div.container
     [:button.button.is-primary.mr-5 {:on-click undo}  "Undo"]
     [:button.button.is-primary.mr-5 {:on-click redo} "Redo"]
     [:button.button.is-danger {:on-click reset} "Reset"]]
    [:div.box.mt-5
     [:div.modal {:class (if @updating "is-active")}
      [:div.modal-background {:on-click remove-erroneous-history}]
      [:div.modal-content
       [:input {:on-change change-diameter :type "range" :value @chosen-radius :step 1 :min 10 :max 80}]]]
     [:canvas {:on-mouse-down handle-click :on-mouse-move get-cursor-path  :width 640 :height 480}]]]])
