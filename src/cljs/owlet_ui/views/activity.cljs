(ns owlet-ui.views.activity
  (:require [owlet-ui.components.activity.back-track :refer [back-track]]
            [owlet-ui.components.activity.title :refer [activity-title]]
            [owlet-ui.components.activity.embed :refer [activity-embed]]
            [owlet-ui.components.activity.info :refer [activity-info]]
            [owlet-ui.components.activity.inspiration :refer [activity-inspiration]]
            [owlet-ui.components.activity.reflection :refer [activity-reflection]]
            [reagent.core :as reagent]))

(defn activity-view []
  (reagent/create-class
    {:component-did-mount
      (fn []
        (js/zadenMagic2))
     :reagent-render
      (fn []
        [:div.height-wrap.activity
          [back-track]
          [:div.activity-wrap
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
              [activity-inspiration]]]])}))
