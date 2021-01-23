(ns seven.components.spreadsheet
  (:require [reagent.core :as r]
            ;[seven.components.spreadsheet.util :as util]
            [seven.components.ui :refer [component-wrapper]]))

(def active-cell-id (r/atom nil))
(def cell-values (r/atom {}))

(def a-to-z (map char (range 97 123)))
(def numbers (vec (range 51)))

; On focus, highlight
(defn set-active-cell [e]
  (let [id (-> e .-target .-id)]
    (reset! active-cell-id id)))

; On change
(defn change-cell-value [coord value]
  (swap! cell-values assoc-in [(keyword coord) :value] value))

;(defn set-function-cell [coord bool]
  ;(swap! cell-values assoc-in [(keyword coord) :function?] bool))

(defn handle-cell-change [e]
  (let [input (.-target e)
        id (.-id input)
        value (.-value input)]
    (change-cell-value id value)))

(add-watch active-cell-id :active-cell-watch #(print %4))
(add-watch cell-values :value-watcher #(print %4))

(defn main []
  (let [active @active-cell-id
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
                            :value (get-in values [(keyword id) :value])
                            :on-change handle-cell-change
                            :on-focus set-active-cell
                            :class (str
                                    "spreadsheet-input"
                                    (if (= id active) "-active"))}]]))
                     a-to-z)])
             numbers)]]]]))
