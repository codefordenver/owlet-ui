(ns owlet-ui.components.activity.challenge
  (:require [cljsjs.showdown]))

(def showdown (js/showdown.Converter.))

(defn activity-challenge [challenge]
  [:div.activity-challenge-wrap.box-shadow
    [:div.list-title
     [:p [:b "⚡⚡ challenge⚡⚡ "]]]
    [:div {"dangerouslySetInnerHTML"
           #js{:__html (.makeHtml showdown challenge)}}]])
