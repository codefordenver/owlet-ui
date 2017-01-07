(ns owlet-ui.components.activity.challenge
  (:require [cljsjs.marked]))

(defn activity-challenge [activity-data]
  (fn []
    (if-let [challenge (get-in @activity-data [:fields :challenge])]
      [:div.activity-challenge-wrap.box-shadow
       [:div.list-title
        [:p [:b "challenge"]]]
       [:div {"dangerouslySetInnerHTML"
              #js{:__html (js/marked challenge)}}]])))
