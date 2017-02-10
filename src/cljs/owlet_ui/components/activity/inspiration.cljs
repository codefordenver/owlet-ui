(ns owlet-ui.components.activity.inspiration
  (:require [cljsjs.showdown]))

(defn activity-inspiration [inspiration]
 (let [showdown (js/showdown.Converter.)]
  [:div.activity-inspiration-wrap.box-shadow
   [:b "Inspiration"]
   [:div {"dangerouslySetInnerHTML"
          #js{:__html (.makeHtml showdown inspiration)}}]]))
