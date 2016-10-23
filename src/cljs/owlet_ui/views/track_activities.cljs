(ns owlet-ui.views.track-activities
  (:require [re-frame.core :as re]
            [reagent.core :as reagent :refer [atom]]
            [owlet-ui.components.activity-thumbnail :refer [activity-thumbnail]]))

(defn track-activities-view []
  (let [active-view (re/subscribe [:activities-by-track-in-view])
        activities (re/subscribe [:activities-by-track])]
    (reagent/create-class
      {:component-will-mount
       (fn []
         (when (empty? @active-view)
            (re/dispatch [:get-library-content])))
       :reagent-render
       (fn []
         (let [{:keys [display-name track-id]} @active-view
               activity-items (get @activities track-id)]
            [:div
              [:div.back-track-wrap
                [:div
                  [:a {:href "#/tracks/"}
                    [:img {:src "img/backtrack.png"}]]]
                [:div
                  [:a {:href "#/tracks/"}
                    [:p "ALL TRACKS"]]]]
              [:div.container-fluid.track-activities-wrap
                [:h1 display-name]
                [:div.flexcontainer-wrap
                  (if (empty? activity-items)
                   [:p "No activities in this track yet. Check back soon."]
                   (for [activity activity-items
                         :let [fields (:fields activity)
                               id (get-in fields [:preview :sys :id])
                               _fields_ (assoc fields :track-id (name track-id))
                               url (:url-safe-name activity)]]
                     ^{:key id} [activity-thumbnail _fields_ url]))]]]))})))
