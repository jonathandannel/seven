(ns seven.components.spreadsheet
  (:require [reagent.core :as r]
            [seven.components.spreadsheet.util :as util]
            [seven.components.ui :refer [component-wrapper]]))

(def active-cell-id (r/atom nil))
(def cell-values (r/atom {}))
(def showing-function-value (r/atom {}))

(def function-cell-map (r/atom {}))
; Shape: {:b2 ["a1" "a2" "a3"]}
; Function @ b2 takes a1:a3 args

(def a-to-z (map char (range 97 123)))
(def numbers (vec (range 51)))

(defn set-active-cell [e]
  (let [id (-> e .-target .-id)]
    (reset! active-cell-id id)))

(defn set-function-cells [coord cells]
  (swap! function-cell-map assoc (keyword coord) cells))

(defn change-cell-value [coord value]
  ; Read function syntax and compute
  (if (util/is-function value)
    (swap! cell-values assoc-in [(keyword coord) :computed]
           (util/read-function value
                               @cell-values set-function-cells coord)))
  ; Just handle changing the literal text value of a cell
  (swap! cell-values assoc-in [(keyword coord) :value] value)
  ; Check if cell belongs to a function, if so recompute function
  (doseq [[k v] @function-cell-map]
    (if (some #(= coord %) v)
      (swap! cell-values assoc-in [(keyword k) :computed]
             (util/read-function
              (get-in @cell-values [(keyword k) :value])
              @cell-values set-function-cells k)))))

(defn handle-cell-change [e]
  (let [input (.-target e)
        id (.-id input)
        value (.-value input)]
    (change-cell-value id value)))

(defn toggle-show-function [id]
  (if ((keyword id) @showing-function-value)
    (swap! showing-function-value dissoc (keyword id))
    (swap! showing-function-value assoc (keyword id) true)))

(defn main []
  (let [active @active-cell-id
        showing-function @showing-function-value
        values @cell-values]
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
                  (if (= x (first active))
                    "spreadsheet-active-letter")}  x])
              a-to-z)]]
       [:tbody
        (map (fn [number]
               [:tr {:key (str "row-" "number-" number)}
                [:th.spreadsheet-title-number
                 {:key (str "title-" number)
                  :class
                  (if (= number (int (last active)))
                    "spreadsheet-active-number")}  number]
                (map (fn [letter]
                       (let [id (str letter number)]
                         [:td {:key (str "cell-td-" id)}
                          [:input.input
                           {:key (str "spreadsheet-input-key-" id)
                            :id id
                            :value
                            (if (get showing-function (keyword id))
                              (get-in values [(keyword id) :computed])
                              (get-in values [(keyword id) :value]))
                            :on-change handle-cell-change
                            :on-double-click #(toggle-show-function id)
                            :on-focus set-active-cell
                            :class (str
                                    "spreadsheet-input"
                                    (if (= id active) "-active"))}]]))
                     a-to-z)])
             numbers)]]]]))
