(ns seven.components.spreadsheet
  (:require [reagent.core :as r]
            [seven.components.spreadsheet.util :as util]))

(def active-cell-id (r/atom nil))
(def cell-values (r/atom {}))
(def formula-cell-map (r/atom {}))
; Key = Formula cell 
; Value = Cells (args) that the formula depends on 
; Ex: {:b2 ["a1" "a2" "a3"]}

; Which cells should show plain :value rather than :computed
(def showing-formula-value (r/atom {}))

(def a-to-z (map char (range 97 123)))
(def numbers (vec (range 51)))

(defn set-active-cell [e]
  (reset! active-cell-id (-> e .-target .-id)))

(defn update-formula-cell-map [coord cells]
  (swap! formula-cell-map assoc (keyword coord) cells))

(defn change-cell-value [coord value]
  ; Read function syntax and compute
  (if (util/is-function value)
    (swap! cell-values assoc-in [(keyword coord) :computed]
           (util/compute-formula
            value
            @cell-values
            update-formula-cell-map
            coord)))
  ; Just handle changing the literal text value of a cell
  (swap! cell-values assoc-in [(keyword coord) :value] value)

  ; If not a formula cell 
  ; Check if cell belongs to a function, if so recompute function
  (doseq [[k v] @formula-cell-map]
    (if (some #(= coord %) v)
      (swap! cell-values assoc-in [(keyword k) :computed]
             (util/compute-formula
              (get-in @cell-values [(keyword k) :value])
              @cell-values
              update-formula-cell-map
              k)))))

(defn handle-cell-change [e]
  (change-cell-value (-> e .-target .-id) (-> e .-target .-value)))

; Set whether a cell shows its formula or value
(defn toggle-show-formula [id]
  (if ((keyword id) @showing-formula-value)
    (swap! showing-formula-value dissoc (keyword id))
    (swap! showing-formula-value assoc (keyword id) true)))

(defn main []
  (let [active-cell @active-cell-id
        showing-formula-value @showing-formula-value
        cell-values @cell-values]
    [:div {:class "card"}
     [:div {:class "card-header"}
      [:div {:class "card-header-title"}
       "Cells"]]
     [:div.spreadsheet-container
      [:table
    ; A-Z titles
       [:thead
        [:tr
         [:th.spreadsheet-title-letter {:key "spacer"} ""]
         (map (fn [x]
                [:th.spreadsheet-title-letter
                 {:key (str "title-" x)
                  :class
                  (if (= x (first active-cell))
                    "spreadsheet-active-letter")}  x])
              a-to-z)]]
       [:tbody
        (map (fn [number]
               [:tr {:key (str "row-" "number-" number)}
                [:th.spreadsheet-title-number
                 {:key (str "title-" number)
                  :class
                  (if (= number (int (last active-cell)))
                    "spreadsheet-active-number")}  number]
                (map (fn [letter]
                       (let [id (str letter number)]
                         [:td {:key (str "cell-td-" id)}
                          [:input.input
                           {:key (str "spreadsheet-input-key-" id)
                            :id id
                            :value
                            (if (get showing-formula-value (keyword id))
                              (get-in cell-values [(keyword id) :computed])
                              (get-in cell-values [(keyword id) :value]))
                            :on-change handle-cell-change
                            :on-double-click #(toggle-show-formula id)
                            :on-focus set-active-cell
                            :class (str
                                    "spreadsheet-input"
                                    (if (= id active-cell) "-active"))}]]))
                     a-to-z)])
             numbers)]]]]))
