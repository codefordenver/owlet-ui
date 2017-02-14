(ns owlet-ui.components.activity.inspiration
  (:require [owlet-ui.helpers :refer [showdown]]))

(defn activity-inspiration [inspiration]
  [:div.activity-inspiration-wrap.box-shadow
   [:b "Inspiration"]
   [:div {"dangerouslySetInnerHTML"
          #js{:__html (.makeHtml showdown inspiration)}}]])
