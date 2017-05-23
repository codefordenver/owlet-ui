(ns owlet-ui.components.activity.info
  (:require [re-com.core :as re-com :refer-macros [handler-fn]]
            [re-com.popover]
            [cljsjs.bootstrap]
            [cljsjs.jquery]
            [reagent.core :as reagent]
            [re-frame.core :as rf]
            [owlet-ui.helpers :refer [showdown]]))

(defn activity-info [platform summary why preRequisites materials]
  (let [showing? (reagent/atom false)
        platform-name (:name platform)
        platform-search-name (:search-name platform)
        platform-color (:color platform)
        set-as-showdown (fn [title field & [class]]
                          [:div {:class class
                                 "dangerouslySetInnerHTML"
                                        #js{:__html (.makeHtml showdown (str title field))}}])]
    [:div.activity-info-wrap.box-shadow
     [:div
       [:b "Platform"] [:br]
       [re-com/popover-anchor-wrapper
        :showing? showing?
        :position :below-left
        :anchor [:button.platform.btn
                 {:on-click #(rf/dispatch [:filter-activities-by-search-term platform-search-name])
                  :style {:background-color platform-color}
                  :on-mouse-over (handler-fn (reset! showing? true))
                  :on-mouse-out  (handler-fn (reset! showing? false))}
                 platform-name]
        :popover [re-com/popover-content-wrapper
                  :close-button? false
                  :body "Click for more info"]]]
     [set-as-showdown "<b>Summary</b><br>" summary]
     (when why
      [set-as-showdown "<b>Why?</b><br>" why])
     (when preRequisites
      [set-as-showdown "<b>Pre-requisites</b><br>" preRequisites])
     (when materials
      [set-as-showdown "<b>Materials</b>" materials "list-title"])]))
