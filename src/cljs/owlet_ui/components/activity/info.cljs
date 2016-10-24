(ns owlet-ui.components.activity.info
  (:require [re-frame.core :as re]
            [cljsjs.marked]
            [cljsjs.bootstrap]
            [cljsjs.jquery]
            [reagent.core :as reagent]))

(defn set-as-marked
  "returns component as markdown"
  [title field & [class]]
  (when field
    [:div {:class class
           "dangerouslySetInnerHTML"
                  #js{:__html (js/marked (str title field))}}]))

;; TODO: figure out how to make this a component function
;; so we can pass tooltip :title as an argument

(def tooltip-component
  ^{:component-did-mount #(.tooltip (js/$ (reagent.core/dom-node %)) #js {:placement "right"
                                                                          :title     "Off-computer activity"})}
  (fn [text]
    [:button.btn.btn-warning text]))

(defn activity-info []
  (let [activity-data (re/subscribe [:activity-in-view])]
    (let [unplugged (get-in @activity-data [:fields :unplugged])
          tech-requirements (get-in @activity-data [:fields :techRequirements])
          summary (get-in @activity-data [:fields :summary])
          why (get-in @activity-data [:fields :why])
          pre-reqs (get-in @activity-data [:fields :preRequisites])
          materials (get-in @activity-data [:fields :materials])]
      [:div.activity-info-wrap
       (if unplugged
         [:p [tooltip-component "UNPLUGGED"]]
         [set-as-marked "<b>Technology</b><br>" tech-requirements])
       [set-as-marked "<b>Summary</b><br>" summary]
       [set-as-marked "<b>Why?</b><br>" why]
       [set-as-marked "<b>Pre-requisites</b><br>" pre-reqs]
       [set-as-marked "<b>Materials</b>" materials "list-title"]])))
