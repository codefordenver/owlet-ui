(ns owlet-ui.components.activity.inspiration
  (:require [re-frame.core :as re]
            [cljsjs.marked]))

(defn activity-inspiration []
  (let [activity-data (re/subscribe [:activity-in-view])]
    (fn []
      (if-let [inspiration (get-in @activity-data [:fields :inspiration])]
        [:div.activity-inspiration-wrap.box-shadow
          [:b "Inspiration"]
          [:div {"dangerouslySetInnerHTML"
                  #js{:__html (js/marked inspiration)}}]]))))
