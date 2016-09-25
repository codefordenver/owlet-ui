(ns owlet-ui.views.activity
  (:require [re-frame.core :as re]))

(defn activity-view []
  (let [activity-data (re/subscribe [:activity-in-view])]
    (fn []
      (let [title (get-in @activity-data [:fields :title])]
        [:div
         [:h1 title]
         [:code (str @activity-data)]]))))