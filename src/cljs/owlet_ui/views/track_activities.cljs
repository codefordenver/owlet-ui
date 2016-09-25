(ns owlet-ui.views.track-activities
  (:require [re-frame.core :as re]
            [reagent.core :as reagent :refer [atom]]
            [owlet-ui.components.activity-thumbnail :refer [activity-thumbnail]]))

(defn- get-display-name
  "find the matching display name for activity in activity model data"
  [models k]
  (let [match (some #(when (= (keyword (:model-id %)) k)
                      (:name %)) models)]
    (.replace match "Activity" "")))

(defn track-activities-view []
  (let [activity-models (re/subscribe [:library-activity-models])
        active-view (re/subscribe [:activities-by-track-in-view])
        activities (re/subscribe [:activities-by-track])]
    (reagent/create-class
      {:reagent-render
       (fn []
         (let [{:keys [display-name track-id]} @active-view
               activity-items (get @activities track-id)]
           (if (or (empty? @activity-models) (nil? display-name))
             (re/dispatch [:get-activity-models])
             (let [models (:models @activity-models)
                   _display-name_ (get-display-name models track-id)]
               (re/dispatch [:set-activities-by-track-in-view :display-name _display-name_])))
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
                 ^{:key id} [activity-thumbnail _fields_]))]]))})))
