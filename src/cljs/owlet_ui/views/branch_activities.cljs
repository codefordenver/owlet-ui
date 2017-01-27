(ns owlet-ui.views.branch-activities
  (:require [re-frame.core :as rf]
            [owlet-ui.components.activity-thumbnail :refer [activity-thumbnail]]
            [owlet-ui.components.activity.breadcrumb :refer [breadcrumb]]))

(defn branch-activities-view []
  (let [{:keys [display-name activities]} @(rf/subscribe [:activities-by-branch-in-view])]
      [:div
        [breadcrumb]
        [:div.container-fluid.branch-activities-wrap
         (if-not display-name
           [:div.activities-wrap
            [:h2 [:mark.white.box.box-shadow [:b "This branch does not exist"]]]]
           [:div.activities-wrap
            [:h2 [:a {:href "#/branches"} [:img.back {:src "img/back-filled.png"}]]
                 [:mark.white.box-shadow [:b display-name]]]
            [:div.flexcontainer-wrap
             (if (seq activities)
               (for [activity activities
                     :let [fields (:fields activity)
                           id (get-in fields [:preview :sys :id] (gensym "key-"))
                           entry-id (get-in activity [:sys :id])]]
                 ^{:key id} [activity-thumbnail fields entry-id])
               [:p.no-activities [:mark "No activities in this branch yet. Check back soon."]])]])]]))
