(ns owlet-ui.views.activity
  (:require [owlet-ui.components.activity.breadcrumb :refer [breadcrumb]]
            [owlet-ui.components.activity.title :refer [activity-title]]
            [owlet-ui.components.activity.embed :refer [activity-embed]]
            [owlet-ui.components.activity.info :refer [activity-info]]
            [owlet-ui.components.activity.inspiration :refer [activity-inspiration]]
            [owlet-ui.components.activity.reflection :refer [activity-reflection]]
            [re-frame.core :as re]))

(defn activity-view []
  (let [activity-data (re/subscribe [:activity-in-view])]
    (fn []
      [:div.activity
        [breadcrumb activity-data]
        [:div.activity-wrap
          [:div.activity-header.col-xs-12
            [activity-title activity-data]]
          [:div.activity-content.col-xs-12.col-lg-8
            [activity-embed activity-data]
            [:div.hidden-sm-down
              [activity-reflection activity-data]]]
          [:div.activity-content.col-xs-12.col-lg-4
            [activity-info activity-data]
            [:div.hidden-md-up
              [activity-reflection activity-data]]
            [activity-inspiration activity-data]]]])))
