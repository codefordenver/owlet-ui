(ns owlet-ui.views.activity
  (:require [owlet-ui.components.activity.title :refer [activity-title]]
            [owlet-ui.components.activity.embed :refer [activity-embed]]
            [owlet-ui.components.activity.info :refer [activity-info]]
            [owlet-ui.components.activity.inspiration :refer [activity-inspiration]]
            [owlet-ui.components.activity.challenge :refer [activity-challenge]]
            [owlet-ui.components.activity.image-gallery :refer [activity-image-gallery]]
            [owlet-ui.components.activity.comments :refer [activity-comments]]
            [re-frame.core :as re]))

(defn activity-view []
  (let [{:keys [fields]} @(re/subscribe [:activity-in-view])]
    (let [{:keys [why
                  title
                  embed
                  author
                  skills
                  summary
                  preview
                  challenge
                  materials
                  unplugged
                  inspiration
                  preRequisites
                  techRequirements
                  image-gallery-urls]} fields]
      (re/dispatch [:set-active-document-title! title])
      [:div.activity
       [:div.activity-wrap
        [:div.activity-header.col-xs-12
         [activity-title title author]]
        [:div.activity-content.col-xs-12.col-lg-8
         [activity-embed embed skills preview]
         (when (seq image-gallery-urls)
          [activity-image-gallery image-gallery-urls])]
        [:div.activity-content.col-xs-12.col-lg-4
         [activity-info unplugged techRequirements summary why preRequisites materials]
         (when challenge
          [activity-challenge challenge])
         (when inspiration
          [activity-inspiration inspiration])]
        [:div.activity-content.col-xs-12 [activity-comments]]]])))
