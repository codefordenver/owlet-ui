(ns owlet-ui.components.activity.info
  (:require [re-com.core :as re-com :refer-macros [handler-fn]]
            [re-com.popover]
            [cljsjs.marked]
            [cljsjs.bootstrap]
            [cljsjs.jquery]
            [reagent.core :as reagent]))

(defn- set-as-marked
  "returns component as markdown"
  [title field & [class]]
  [:div {:class class
         "dangerouslySetInnerHTML"
                #js{:__html (js/marked (str title field))}}])

(defn activity-info [unplugged techRequirements summary
                     why preRequisites materials]
  (let [showing? (reagent/atom false)]
    [:div.activity-info-wrap.box-shadow
     (if unplugged
       [re-com/popover-anchor-wrapper
        :showing? showing?
        :position :right-below
        :anchor [:button
                 {:class         "btn btn-warning"
                  :style         {:margin-bottom "10px"}
                  :on-mouse-over (handler-fn (reset! showing? true))
                  :on-mouse-out  (handler-fn (reset! showing? false))}
                 "UNPLUGGED"]
        :popover [re-com/popover-content-wrapper
                  :close-button? false
                  :title "What does this mean?"
                  :body "UNPLUGGED activities do not require a computer or device"]]
       [set-as-marked "<b>Platform</b><br>" techRequirements])
     [set-as-marked "<b>Summary</b><br>" summary]
     (when why
      [set-as-marked "<b>Why?</b><br>" why])
     (when preRequisites
      [set-as-marked "<b>Pre-requisites</b><br>" preRequisites])
     (when materials
      [set-as-marked "<b>Materials</b>" materials "list-title"])]))
