(ns owlet-ui.components.activity.info
  (:require [re-frame.core :as re]
            [cljsjs.marked]
            [reagent.core :as reagent :refer [atom]]))

(defn set-as-marked
  "returns component as markdown"
  [title field & [class]]
  (when field
    [:div {:class class
           "dangerouslySetInnerHTML"
           #js{:__html (js/marked (str title field))}}]))

(defn activity-info []
  (let [activity-data (re/subscribe [:activity-in-view])]
    (reagent/create-class
       {:component-did-mount
        (fn [])
        :reagent-render
        (fn []
          (let [unplugged (get-in @activity-data [:fields :unplugged])
                tech-requirements (get-in @activity-data [:fields :techRequirements])
                summary (get-in @activity-data [:fields :summary])
                why (get-in @activity-data [:fields :why])
                pre-reqs (get-in @activity-data [:fields :preRequisites])
                materials (get-in @activity-data [:fields :materials])]
            [:div.activity-info-wrap
              (if unplugged
                [:p [:button.btn.btn-warning.foo "UNPLUGGED"]]
                [set-as-marked "<b>Technology</b><br>" tech-requirements])
              [set-as-marked "<b>Summary</b><br>" summary]
              [set-as-marked "<b>Why?</b><br>" why]
              [set-as-marked "<b>Pre-requisites</b><br>" pre-reqs]
              [set-as-marked "<b>Materials</b>" materials "list-title"]]))})))
