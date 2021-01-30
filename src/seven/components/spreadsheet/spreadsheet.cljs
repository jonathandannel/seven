(ns seven.components.spreadsheet
  (:require [reagent.core :as r]
            [seven.components.spreadsheet.util :as util]))

(def active-cell-id (r/atom nil))
(def cell-values (r/atom {}))
(def formula-cell-map (r/atom {}))
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

(defn edit-cell [coord]
  (reset! active-cell-id coord)
  (reset! is-editing true))

(defn change-cell-value [coord value]
  (swap! cell-values assoc-in [(keyword coord) :value] value)
  (if (< (count value) 1)
    (swap! cell-values assoc-in [(keyword coord) :computed] nil))
  (recompute-fn-cell coord value))

(defn handle-cell-change [e reset-active?]
  (change-cell-value (-> e .-target .-id) (-> e .-target .-value))
  (reset! is-editing false)
  (if reset-active? (reset! active-cell-id nil)))

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
             (if (and @active-cell-id (= number (int (last @active-cell-id))))
               "spreadsheet-active-number")} number]
           (doall
            (map
             (fn [letter]
               (let [id (str letter number)
                     cell (get @cell-values (keyword id))
                     value (get cell :value)
                     computed (get cell :computed)
                     active (= id @active-cell-id)]
                 [:td {:key (str "cell-td-" id)}
                  [:span.input.spreadsheet-input-base {:on-click #(edit-cell id)}
                   (if (or (not @is-editing) (not active))
                     [:span.input.is-flex.is-justify-content-center.spreadsheet-input-display
                      {:on-mouse-down #(reset! is-editing true)
                       :class (if computed " computed-cell-val" "")}
                      (or computed value)])
                   (if (and @is-editing active)
                     [:input.input.spreadsheet-input.spreadsheet-input-textarea
                      {:key (str "spreadsheet-input-key-" id)
                       :id id
                       :on-key-down #(if (= (-> % .-key) "Enter")
                                       (handle-cell-change % :reset))
                       :auto-focus true
                       :default-value value
                       :on-blur handle-cell-change}])]]))
             a-to-z))])
        numbers))]]]])
