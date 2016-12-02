(ns owlet-ui.components.activity.inspiration
  (:require [cljsjs.marked]))

(defn activity-inspiration [activity-data]
  (fn []
    (if-let [inspiration (get-in @activity-data [:fields :inspiration])]
      [:div.activity-inspiration-wrap.box-shadow
       [:b "Inspiration"]
       [:div {"dangerouslySetInnerHTML"
              #js{:__html (js/marked inspiration)}}]])))

