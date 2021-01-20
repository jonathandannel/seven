(ns seven.components.ui)

(defn component-wrapper [title component]
  [:div {:class "card"}
   [:div {:class "card-header"}
    [:div {:class "card-header-title"}
     title]]
   [:div {:class "card-content" :style {:overflow "scroll" :max-height 500}}
    component]])
