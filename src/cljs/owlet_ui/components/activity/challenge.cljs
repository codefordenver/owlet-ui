(ns owlet-ui.components.activity.challenge
  (:require [cljsjs.marked]))

(defn activity-challenge [challenge]
  (when challenge
    [:div.activity-challenge-wrap.box-shadow
      [:div.list-title
       [:p [:b "⚡⚡ challenge⚡⚡"]]]
      [:div {"dangerouslySetInnerHTML"
             #js{:__html (js/marked challenge)}}]]))
