(ns seven.components.spreadsheet
  (:require [reagent.core :as r]
            [seven.components.spreadsheet.util :as util]))

(def active-cell-id (r/atom nil))
(def cell-values (r/atom {}))
(def formula-cell-map (r/atom {}))
(def showing-formula-value (r/atom {}))
(def is-editing (r/atom false))

(def a-to-z (map char (range 97 123)))
(def numbers (vec (range 101)))

(defn set-active-cell [e]
  (reset! active-cell-id (-> e .-target .-id)))

(defn update-formula-cell-map [coord cells]
  (swap! formula-cell-map assoc (keyword coord) cells))

(defn recompute-fn-cell [coord value]
  (if (util/is-function value)
    (swap! cell-values assoc-in [(keyword coord) :computed]
           (util/compute-formula
            value
            @cell-values
            update-formula-cell-map
            coord)))
  (doseq [[k v] @formula-cell-map]
    (if (some #(= coord %) v)
      (swap! cell-values assoc-in [(keyword k) :computed]
             (util/compute-formula
              (get-in @cell-values [(keyword k) :value])
              @cell-values
              update-formula-cell-map
              k)))))

(defn change-cell-value [coord value]
  (swap! cell-values assoc-in [(keyword coord) :value] value)
  (if (< (count value) 1)
    (do
      (swap! showing-formula-value dissoc (keyword coord))
      (swap! cell-values assoc-in [(keyword coord) :computed] nil)))
  ; Check if cell is or belongs to a function, act accordingly
  (recompute-fn-cell coord value))

(defn handle-cell-change [e]
  (change-cell-value (-> e .-target .-id) (-> e .-target .-value)))

(defn toggle-show-formula [id]
  (if (get-in @cell-values [(keyword id) :computed])
    (if ((keyword id) @showing-formula-value)
      (swap! showing-formula-value dissoc (keyword id))
      (swap! showing-formula-value assoc (keyword id) true))))

(add-watch cell-values :cvwatch #(print %4))

(defn main []
  [:div {:class "card"}
   [:div {:class "card-header"}
    [:div {:class "card-header-title"}
     "Cells"]]
   [:div.spreadsheet-container
    [:table
     [:thead
      [:tr
       [:th.spreadsheet-title-letter {:key "spacer"} ""]
       (doall
        (map
         (fn [x]
           [:th.spreadsheet-title-letter
            {:key (str "title-" x)
             :class
             (if (= x (first @active-cell-id))
               "spreadsheet-active-letter")}  x])
         a-to-z))]]
     [:tbody
      (doall
       (map
        (fn [number]
          [:tr {:key (str "row-" "number-" number)}
           [:th.spreadsheet-title-number
            {:key (str "title-" number)
             :class
             (if (= number (int (last @active-cell-id)))
               "spreadsheet-active-number")}  number]
           (doall
            (map
             (fn [letter]
               (let [id (str letter number)
                     cell (get @cell-values (keyword id))
                     value (get cell :value)
                     computed (get cell :computed)
                     show-form-val (get @showing-formula-value (keyword id))]
                 [:td {:key (str "cell-td-" id)}
                  [:span.input {:class "spreadsheet-input"
                                :on-click (fn []
                                            (reset! active-cell-id id)
                                            (if (not show-form-val)
                                              (reset! is-editing true)))}
                   (if (or (not @is-editing) (not (= id @active-cell-id)))
                     [:span.input
                      {:on-double-click (fn []
                                          (reset! is-editing false)
                                          (toggle-show-formula id))
                       :on-mouse-down #(reset! is-editing true)
                       :style {:display "flex" :justify-content "center"}
                       :class (str
                               "spreadsheet-input"
                               (if computed
                                 (if show-form-val
                                   " cell-formula" " cell-val")
                                 "")
                               (if (= id active-cell-id)
                                 " active-cell"))}
                      (if show-form-val
                        computed
                        value)])
                   (if (and @is-editing (= id @active-cell-id) (not show-form-val))
                     [:input.input
                      {:key (str "spreadsheet-input-key-" id)
                       :id id
                       :style {:display "flex" :justify-content "center" :text-align "center"}
                       :on-key-down (fn [e]
                                      (if (= (-> e .-key) "Enter")
                                        (handle-cell-change e)))
                       :auto-focus true
                       :default-value value
                       :on-blur (fn [e]
                                  (if (not show-form-val)
                                    (do
                                      (handle-cell-change e)
                                      (reset! is-editing false))))
                       :class (str
                               "spreadsheet-input"
                               (if computed
                                 (if showing-formula-value
                                   " cell-formula" " cell-val")
                                 "")
                               (if (= id active-cell-id)
                                 " active-cell"))}])]]))
             a-to-z))])
        numbers))]]]])
