(ns owlet-ui.views.branch-activities
  (:require [re-frame.core :as re]
            [reagent.core :as reagent :refer [atom]]
            [owlet-ui.components.activity-thumbnail :refer [activity-thumbnail]]))

(defn branch-activities-view []
  (let [active-view (re/subscribe [:activities-by-branch-in-view])
        activities (re/subscribe [:activities-by-branch])]
    (reagent/create-class
      {:component-will-mount
       (fn []
         (when (empty? @active-view)
            (re/dispatch [:get-library-content])))
       :reagent-render
       (fn []
         (let [{:keys [display-name track-id]} @active-view
               activity-items (get @activities track-id)]
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
                    (if (empty? activity-items)
                     [:p.no-activities [:mark "No activities in this branch yet. Check back soon."]]
                     (for [activity activity-items
                           :let [fields (:fields activity)
                                 id (get-in fields [:preview :sys :id] (gensym "key-"))
                                 _fields_ (assoc fields :track-id (name track-id))
                                 entry-id (get-in activity [:sys :id])]]
                      ^{:key id} [activity-thumbnail _fields_ entry-id]))]]]]))})))
