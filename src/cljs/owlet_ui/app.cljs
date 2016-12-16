(ns owlet-ui.app
  (:require [re-frame.core :as re]
            [reagent.core :as reagent]
            [owlet-ui.components.upload-image-modal :refer [upload-image-component]]
            [owlet-ui.components.sidebar :refer [sidebar-component]]
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

(def show? (reagent/atom false))

(defn main-view []
  (let [active-view (re/subscribe [:active-view])
        loading? (re/subscribe [:set-loading-state?])
        src (re/subscribe [:user-has-background-image?])
        is-user-logged-in? (re/subscribe [:is-user-logged-in?])
        open-modal (fn [_] (reset! show? true))
        close-modal (fn [_] (reset! show? false))]
    (reagent/create-class
      {:component-will-mount
       #(re/dispatch [:get-auth0-profile])
       :reagent-render
       (fn []
         (if (= @active-view :welcome-view)
           [show-view @active-view]
           [:div#main
            [sidebar-component]
            [:div.content {:style {:width "100%"
                                   :background-image (str "url(" @src ")")
                                   :background-size "cover"}}
             [upload-image-component show? close-modal]
             [:button#change-header-btn
              {:type     "button"
               :class    "btn btn-secondary"
               :style    {:font-size "1em"
                          :padding "6px"
                          :display (if @is-user-logged-in?
                                     "block"
                                     "none")}
               :on-click open-modal}
              [:i.fa.fa-pencil-square-o]]
             (when @loading?
               [loading-component])
             [show-view @active-view]]]))})))
