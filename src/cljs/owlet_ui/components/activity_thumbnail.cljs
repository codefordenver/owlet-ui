(ns owlet-ui.components.activity-thumbnail)

(defn activity-thumbnail [title picture]
  [:div
   [:h3 title]
   [:img {:src picture :width "40%"}]])
