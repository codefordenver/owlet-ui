(ns owlet-ui.components.activity.info
  (:require [re-frame.core :as re]
            [cljsjs.marked]))

(defn activity-info []
  (let [activity-data (re/subscribe [:activity-in-view])]
    (fn []
      (let [tech-requirements (get-in @activity-data [:fields :techRequirements])
            summary (get-in @activity-data [:fields :summary])
            why (get-in @activity-data [:fields :why])
            pre-reqs (get-in @activity-data [:fields :preRequisites])
            materials (get-in @activity-data [:fields :materials])]
        [:div.activity-info-wrap
          (if (not (nil? tech-requirements))
            [:div {"dangerouslySetInnerHTML"
                    #js{:__html (js/marked (str "<b>Technology:</b> " tech-requirements))}}])
          (if (not (nil? summary))
            [:div {"dangerouslySetInnerHTML"
                    #js{:__html (js/marked (str "<b>Summary:</b> " summary))}}])
          (if (not (nil? why))
            [:div {"dangerouslySetInnerHTML"
                    #js{:__html (js/marked (str "<b>Why?</b> " why))}}])
          (if (not (nil? pre-reqs))
            [:div {"dangerouslySetInnerHTML"
                    #js{:__html (js/marked (str "<b>Pre-requisites:</b> " pre-reqs))}}])
          (if (not (nil? materials))
            [:div {"dangerouslySetInnerHTML"
                    #js{:__html (js/marked (str "<b>Materials:</b> " materials))}}])]))))

; TODO: hide field if nil
