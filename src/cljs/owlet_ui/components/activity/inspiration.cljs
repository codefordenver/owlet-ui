(ns owlet-ui.components.activity.inspiration
  (:require [cljsjs.showdown]))

(def showdown (js/showdown.Converter.))

(defn activity-inspiration [inspiration]
  [:div.activity-inspiration-wrap.box-shadow
   [:b "Inspiration"]
   [:div {"dangerouslySetInnerHTML"
          #js{:__html (.makeHtml showdown inspiration)}}]])
