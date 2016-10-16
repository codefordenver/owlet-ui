(ns owlet-ui.app
  (:require [re-frame.core :as re]
            [reagent.core :as reagent]
            [owlet-ui.components.sidebar :refer [sidebar-component]]
            [owlet-ui.components.header :refer [header-component]]
            [owlet-ui.components.loading :refer [loading-component]]
            [owlet-ui.views.welcome :refer [welcome-view]]
            [owlet-ui.views.activity :refer [activity-view]]
            [owlet-ui.views.tracks :refer [tracks-view]]
            [owlet-ui.views.settings :refer [settings-view]]
            [owlet-ui.views.track-activities :refer [track-activities-view]]))

(defmulti views identity)
(defmethod views :welcome-view [] [welcome-view])
(defmethod views :activity-view [] [activity-view])
(defmethod views :tracks-view [] [tracks-view])
(defmethod views :track-activities-view [] [track-activities-view])
(defmethod views :settings-view [] [settings-view])
(defmethod views :default [] [:div])

(defn show-view
  [view-name]
  [views view-name])

(defn main-view []
  (let [active-view (re/subscribe [:active-view])
        loading? (re/subscribe [:set-loading-state?])]
    (reagent/create-class
      {:component-will-mount
       #(re/dispatch [:get-auth0-profile])
       :reagent-render
       (fn []
         [:div#main
          [sidebar-component]
          [:div.content
           [header-component]
           (when @loading?
             [loading-component])
           [show-view @active-view]]])})))
