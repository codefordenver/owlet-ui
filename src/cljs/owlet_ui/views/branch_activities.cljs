(ns owlet-ui.views.branch-activities
  (:require [re-frame.core :as re]
            [reagent.core :as reagent :refer [atom]]
            [owlet-ui.components.activity-thumbnail :refer [activity-thumbnail]]))

(defn branch-activities-view []
  (let [active-branch-activities (re/subscribe [:activities-by-branch-in-view])
        activities-by-branch (re/subscribe [:activities-by-branch])]
    (reagent/create-class
      {:component-will-mount
       (fn []
         (when (empty? @active-branch-activities)
            (re/dispatch [:get-library-content])))
       :reagent-render
       (fn []
         (prn @active-branch-activities)
         (let [{:keys [display-name activities]} @active-branch-activities]
            [:div.outer-height-wrap
              [:div.inner-height-wrap
                [:div.breadcrumb-wrap
                  [:div
                    [:a {:href "#/branches"}
                      [:img {:src "img/back.png"}]]]
                  [:div
                    [:a {:href "#/branches"}
                      [:p "ALL BRANCHES"]]]]
                [:div.container-fluid.branch-activities-wrap
                  [:h2 [:mark.white.box-shadow [:b display-name]]]
                  [:div.flexcontainer-wrap
                    (if (empty? activities)
                     [:p.no-activities [:mark "No activities in this branch yet. Check back soon."]]
                     (for [activity activities
                           :let [fields (:fields activity)
                                 id (get-in fields [:preview :sys :id] (gensym "key-"))
                                 entry-id (get-in activity [:sys :id])]]
                      ^{:key id} [activity-thumbnail fields entry-id]))]]]]))})))
