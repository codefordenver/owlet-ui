(ns owlet-ui.components.activity.info
  (:require [re-frame.core :as re]
            [re-com.core :as re-com
              :refer-macros [handler-fn]]
            [re-com.popover]
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

(defn activity-info []
  (let [activity-data (re/subscribe [:activity-in-view])]
    (let [showing? (reagent/atom false)
          unplugged (get-in @activity-data [:fields :unplugged])
          tech-requirements (get-in @activity-data [:fields :techRequirements])
          summary (get-in @activity-data [:fields :summary])
          why (get-in @activity-data [:fields :why])
          pre-reqs (get-in @activity-data [:fields :preRequisites])
          materials (get-in @activity-data [:fields :materials])]
      [:div.activity-info-wrap.box-shadow
       (if unplugged
          [re-com/popover-anchor-wrapper
             :showing? showing?
             :position :right-below
             :anchor   [:button
                        {:class "btn btn-warning"
                         :style {:margin-bottom "10px"}
                         :on-mouse-over (handler-fn (reset! showing? true))
                         :on-mouse-out  (handler-fn (reset! showing? false))}
                        "UNPLUGGED"]
             :popover  [re-com/popover-content-wrapper
                        :close-button? false
                        :title    "What does this mean?"
                        :body     "UNPLUGGED activities do not require a computer or device"]]
         [set-as-marked "<b>Technology</b><br>" tech-requirements])
       [set-as-marked "<b>Summary</b><br>" summary]
       [set-as-marked "<b>Why?</b><br>" why]
       [set-as-marked "<b>Pre-requisites</b><br>" pre-reqs]
       [set-as-marked "<b>Materials</b>" materials "list-title"]])))
