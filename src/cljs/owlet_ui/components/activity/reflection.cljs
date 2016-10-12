(ns owlet-ui.components.activity.reflection
  (:require [re-frame.core :as re]
            [cljsjs.marked]))

(defn activity-reflection []
  (let [activity-data (re/subscribe [:activity-in-view])]
    (fn []
      (if-let [reflection (get-in @activity-data [:fields :reflectionQuestions])]
        [:div.activity-reflection-wrap
          [:b "Reflection Questions: "]
          [:div {"dangerouslySetInnerHTML"
                  #js{:__html (js/marked reflection)}}]]))))
