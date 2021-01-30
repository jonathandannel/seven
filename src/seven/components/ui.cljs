(ns seven.components.ui)

(def fullwidth-components ["Circle drawer" "Spreadsheet"])

(defn component-wrapper [title component]
  [:div.card.container.component {:style {:width (if (some #(= % title) fullwidth-components) "auto" "60%")}}
   [:div {:class "card-header"}
    [:div {:class "card-header-title"}
     title]]
   [:div {:class "card-content"}
    component]])
