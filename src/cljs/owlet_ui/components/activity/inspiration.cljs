(ns owlet-ui.components.activity.inspiration
  (:require [cljsjs.marked]))

(defn activity-inspiration [inspiration]
  [:div.activity-inspiration-wrap.box-shadow
   [:b "Inspiration"]
   [:div {"dangerouslySetInnerHTML"
          #js{:__html (js/marked (or inspiration ""))}}]])