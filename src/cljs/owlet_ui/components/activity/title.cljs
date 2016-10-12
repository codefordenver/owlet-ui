(ns owlet-ui.components.activity.title
  (:require [re-frame.core :as re]))

(defn activity-title [data]
  (let [activity-data (re/subscribe [:activity-in-view])]
    (fn []
      (let [title (get-in @activity-data [:fields :title])
            author (get-in @activity-data [:fields :author])]
        [:div
         [:h1 title]
         [:h5 "by: " author]]))))
