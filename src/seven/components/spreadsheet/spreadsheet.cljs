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

(defn recompute-formula-cells [coord value]
  (when (util/is-function value)
    (swap! cell-values assoc-in [(keyword coord) :computed]
           (util/compute-formula
            value
            @cell-values
            update-formula-cell-map
            coord)))
  (doseq [[k v] @formula-cell-map]
    (when (some #(= coord %) v)
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
  (when (< (count value) 1)
    (swap! cell-values assoc-in [(keyword coord) :computed] nil))
  (recompute-formula-cells coord value))

(defn handle-cell-change [e reset-active?]
  (change-cell-value (-> e .-target .-id) (-> e .-target .-value))
  (reset! is-editing false)
  (when reset-active? (reset! active-cell-id nil)))

(defn main []
  [:<>
   [:article.message.is-info
    [:div.message-header "Usage and syntax"]
    [:div.message-body
     [:div.container.pl-3
      [:ul.bulleted
       [:li [:span "Supported operations: "
             [:strong "=sum() =sub() =mul() =div() =avg()"]]]
       [:li [:span "Pass formula arguments as a range "
             [:strong "(b1:b5),"]
             " as single cells " [:strong "(c2, d5),"]
             " or a mix of both " [:strong "(c2:c5, e3)"]]]
       [:li [:span "Ex: "
             [:strong "=sum(d1, d2, e1:e5)"]]]]]]]
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
          (fn [letter]
            [:th.spreadsheet-title-letter
             {:key (str "title-" letter)
              :class
              (if (= letter (first @active-cell-id))
                "spreadsheet-active-letter")}  letter])
          a-to-z))]]
      [:tbody
       (doall
        (map
         (fn [number]
           [:tr {:key (str "row-" "number-" number)}
            [:th.spreadsheet-title-number
             {:key (str "title-" number)
              :class
              (when
               (and
                @active-cell-id
                (= number (int (last @active-cell-id))))
                "spreadsheet-active-number")}
             number]
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
                    (when (or (not @is-editing) (not active))
                     ; Face value
                      [:span.input.is-flex.is-justify-content-center.spreadsheet-input-display
                       {:on-mouse-down #(reset! is-editing true)
                        :class (when computed " computed-cell-val")}
                       (or computed value)])
                    (when (and @is-editing active)
                     ; Input
                      [:input.input.spreadsheet-input.spreadsheet-input-textarea
                       {:key (str "spreadsheet-input-key-" id)
                        :id id
                        :class (when (and @is-editing active computed)
                                 " formula-input-val")
                        :on-key-down #(when (= (-> % .-key) "Enter")
                                        (handle-cell-change % :reset))
                        :auto-focus true
                        :default-value value
                        :on-blur handle-cell-change}])]]))
              a-to-z))])
         numbers))]]]]])
