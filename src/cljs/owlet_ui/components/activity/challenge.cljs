(ns owlet-ui.components.activity.challenge
  (:require [cljsjs.marked]))

(defn activity-challenge [challenge]
  [:div.activity-challenge-wrap.box-shadow
   [:div.list-title
    [:p [:b "challenges"]]]
   [:div {"dangerouslySetInnerHTML"
          #js{:__html (js/marked (or challenge ""))}}]])
