(ns owlet-ui.views.activity
  (:require [owlet-ui.components.activity.title :refer [activity-title]]
            [owlet-ui.components.activity.embed :refer [activity-embed]]
            [owlet-ui.components.activity.info :refer [activity-info]]
            [owlet-ui.components.activity.inspiration :refer [activity-inspiration]]
            [owlet-ui.components.activity.reflection :refer [activity-reflection]]))

(defn activity-view []
  (fn []
    [:div.activity-wrap
      [:div.activity-header.col-xs-12
        [activity-title]]
      [:div.activity-content.col-xs-12.col-lg-6
        [activity-embed]
        [:div.hidden-md-down
          [activity-reflection]]]
      [:div.activity-content.col-xs-12.col-lg-6
        [activity-info]
        [:div.hidden-lg-up
          [activity-reflection]]
        [activity-inspiration]]]))
