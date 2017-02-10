(ns owlet-ui.components.activity.challenge
  (:require [cljsjs.showdown]))

(defn activity-challenge [challenge]
 (let [showdown (js/showdown.Converter.)]
  [:div.activity-challenge-wrap.box-shadow
    [:div.list-title
     [:p [:b "⚡⚡ challenge⚡⚡"]]]
    [:div {"dangerouslySetInnerHTML"
           #js{:__html (.makeHtml showdown challenge)}}]]))
