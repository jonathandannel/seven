(ns seven.components.ui)

(def fullwidth-components ["Circle drawer" "Spreadsheet"])

(defn component-wrapper [title component]
  [:div.card.container.component
   {:style
    {:max-width
     (if (some #(= % title) fullwidth-components)
                  "1000px" 
                  "500px")}}
   [:div.card-header
    [:div.card-header-title 
     title]]
   [:div.card-content  
    component]])
