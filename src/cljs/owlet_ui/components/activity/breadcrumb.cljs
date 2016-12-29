(ns owlet-ui.components.activity.breadcrumb)

;; TODO: talk to Trinh about navigation ux/ui
(defn breadcrumb []
  [:div.breadcrumb-wrap
   [:div
    [:a {:href "#/branches"}
     [:img {:src "img/back.png"}]]]
   [:div
    [:a {:href "#/branches"}
     [:p "all activities"]]]])

