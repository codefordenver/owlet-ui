(ns owlet-ui.components.activity.info
  (:require [re-frame.core :as re]
            [cljsjs.marked]))

(defn set-as-marked
  "returns componet as markdown"
  [title field]
  (when field
    [:div {"dangerouslySetInnerHTML"
           #js{:__html (js/marked (str title field))}}]))

(defn activity-info []
  (let [activity-data (re/subscribe [:activity-in-view])]
    (fn []
      (let [unplugged (get-in @activity-data [:fields :unplugged])
            tech-requirements (get-in @activity-data [:fields :techRequirements])
            summary (get-in @activity-data [:fields :summary])
            why (get-in @activity-data [:fields :why])
            pre-reqs (get-in @activity-data [:fields :preRequisites])
            materials (get-in @activity-data [:fields :materials])]
        [:div.activity-info-wrap
         (if unplugged
           [:p [:button "UNPLUGGED"]]
           [set-as-marked "<b>Technology:</b> " tech-requirements])
         [set-as-marked "<b>Summary:</b> " summary]
         [set-as-marked "<b>Why?</b> " why]
         [set-as-marked "<b>Pre-requisites:</b> " pre-reqs]
         [set-as-marked "<b>Materials:</b> " materials]]))))
