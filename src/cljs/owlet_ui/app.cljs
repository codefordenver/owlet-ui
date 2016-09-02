(ns owlet-ui.app
    (:require [re-frame.core :as re-frame]
              [owlet-ui.config :as config]
              [owlet-ui.components.sidebar :refer [sidebar-component]]
              [owlet-ui.components.header :refer [header-component]]
              [owlet-ui.views.welcome :refer [welcome-view]]
              [owlet-ui.views.library :refer [library-view]]
              [owlet-ui.views.tracks :refer [tracks-view]]
              [owlet-ui.views.settings :refer [settings-view]]))

(defmulti views identity)
(defmethod views :welcome-view [] [welcome-view])
(defmethod views :library-view [] [library-view])
(defmethod views :tracks-view [] [tracks-view])
(defmethod views :settings-view [] [settings-view])
(defmethod views :default [] [:div])

(defn show-view
  [view-name]
  [views view-name])

(defn main-view []
  (re-frame/dispatch [:get-auth0-profile])
  (let [active-view (re-frame/subscribe [:active-view])]
    (fn []
      [:div#main
        [sidebar-component]
        [:div.content
          [header-component]
          [show-view @active-view]]])))
