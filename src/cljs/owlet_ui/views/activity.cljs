(ns owlet-ui.views.activity
  (:require [owlet-ui.components.activity.title :refer [activity-title]]
            [owlet-ui.components.activity.embed :refer [activity-embed]]
            [owlet-ui.components.activity.info :refer [activity-info]]))

(defn activity-view []
  (fn []
    [:div.activity-wrap
      [activity-title]
      [activity-embed]
      [activity-info]]))
