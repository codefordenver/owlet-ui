(ns owlet-ui.views.activity
  (:require [owlet-ui.components.activity.back_track :refer [back-track]]
            [owlet-ui.components.activity.title :refer [activity-title]]
            [owlet-ui.components.activity.embed :refer [activity-embed]]
            [owlet-ui.components.activity.info :refer [activity-info]]
            [owlet-ui.components.activity.inspiration :refer [activity-inspiration]]
            [owlet-ui.components.activity.reflection :refer [activity-reflection]]))

(defn activity-view []
  (fn []
<<<<<<< HEAD
    [:div.activity-wrap
      [:div.back-track-wrap
        [back-track]]
=======
    [:div.activity-wrap 
      [back-track]
>>>>>>> 760ee9123108ef3fbe9fec37e2395bb4187dc1d1
      [:div.activity-header.col-xs-12
        [activity-title]]
      [:div.activity-content.col-xs-12.col-lg-8
        [activity-embed]
        [:div.hidden-sm-down
          [activity-reflection]]]
      [:div.activity-content.col-xs-12.col-lg-4
        [activity-info]
        [:div.hidden-md-up
          [activity-reflection]]
        [activity-inspiration]]]))
