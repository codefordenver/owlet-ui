(ns owlet-ui.views.activity
  (:require [owlet-ui.components.activity.breadcrumb :refer [breadcrumb]]
            [owlet-ui.components.activity.title :refer [activity-title]]
            [owlet-ui.components.activity.embed :refer [activity-embed]]
            [owlet-ui.components.activity.info :refer [activity-info]]
            [owlet-ui.components.activity.inspiration :refer [activity-inspiration]]
            [owlet-ui.components.activity.challenge :refer [activity-challenge]]
            [owlet-ui.components.activity.image-gallery :refer [activity-image-gallery]]
            [re-frame.core :as re]))

(defn activity-view []
  (let [{:keys [fields]} @(re/subscribe [:activity-in-view])]
    (let [{why                :why
           title              :title
           embed              :embed
           author             :author
           skills             :skills
           summary            :summary
           preview            :preview
           challenge          :challenge
           materials          :materials
           unplugged          :unplugged
           inspiration        :inspiration
           preRequisites      :preRequisites
           techRequirements   :techRequirements
           image-gallery-urls :image-gallery-urls} fields]
      [:div.activity
       [breadcrumb]
       [:div.activity-wrap
        [:div.activity-header.col-xs-12
         [activity-title title author]]
        [:div.activity-content.col-xs-12.col-lg-8
         [activity-embed embed skills preview]
         [activity-image-gallery image-gallery-urls]]
        [:div.activity-content.col-xs-12.col-lg-4
         [activity-info unplugged techRequirements summary why preRequisites materials]
         [activity-challenge challenge]
         [activity-inspiration inspiration]]]])))
