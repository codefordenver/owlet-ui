(ns owlet-ui.components.activity.info
  (:require [re-frame.core :as re]))

(defn activity-info []
  (let [activity-data (re/subscribe [:activity-in-view])]
    (fn []
      (let [tech-requirements (get-in @activity-data [:fields :techRequirements])
            summary (get-in @activity-data [:fields :summary])
            why (get-in @activity-data [:fields :why])
            pre-reqs (get-in @activity-data [:fields :preRequisites])
            materials (get-in @activity-data [:fields :materials])]
        [:div.activity-info-wrap
          [:p "Technology: " tech-requirements]
          [:p "Summary: " summary]
          [:p "Why? " why]
          [:p "Pre-requisites: " pre-reqs]
          [:p "Materials: " materials]]))))

; TODO: hide field if nil
