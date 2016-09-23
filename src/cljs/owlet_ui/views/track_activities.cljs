(ns owlet-ui.views.track-activities
  (:require [re-frame.core :as re]
            [owlet-ui.components.activity-thumbnail :refer [activity-thumbnail]]))

(defn track-activities-view []
  (let [active-view (re/subscribe [:activities-by-track-in-view])
        activities (re/subscribe [:activities-by-track])]
    (fn []
      (let [{:keys [display-name track-id]} @active-view
            activity-items (get @activities track-id)]
        [:div.jumbotron
         [:div.container-fluid
          [:div.row
           [:div.col-lg-12
            [:h1 display-name]
            [:br]]]
          (if (empty? activity-items)
            [:p "No activities in this track yet. Check back soon."]
            (for [activity activity-items
                  :let [fields (:fields activity)
                        id (get-in fields [:preview :sys :id])
                        _fields_ (assoc fields :track-id (name track-id))]]
              ^{:key id} [activity-thumbnail _fields_]))]]))))