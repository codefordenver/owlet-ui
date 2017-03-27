(ns owlet-ui.app
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]
            [owlet-ui.components.upload-image-modal :refer [upload-image-component]]
            [owlet-ui.components.sidebar :refer [sidebar-component]]
            [owlet-ui.components.lpsidebar :refer [lpsidebar-component]]
            [owlet-ui.components.loading :refer [loading-component]]
            [owlet-ui.views.welcome :refer [welcome-view]]
            [owlet-ui.components.search-bar :refer [search-bar]]
            [owlet-ui.views.not-found :refer [not-found-view]]
            [owlet-ui.views.activity :refer [activity-view]]
            [owlet-ui.views.branches :refer [branches-view]]
            [owlet-ui.views.settings :refer [settings-view]]
            [owlet-ui.async :as async]
            [owlet-ui.auth0 :as auth0]
            [owlet-ui.config :as config]
            [owlet-ui.firebase :as fb]
            [owlet-ui.views.search-results :refer [search-results-view]]
            [owlet-ui.views.branch-activities :refer [branch-activities-view]]))

(defmulti views identity)
(defmethod views :welcome-view [] [welcome-view])
(defmethod views :not-found-view [] [not-found-view])
(defmethod views :search-results-view [] [search-results-view])
(defmethod views :activity-view [] [activity-view])
(defmethod views :branches-view [] [branches-view])
(defmethod views :branch-activities-view [] [branch-activities-view])
(defmethod views :settings-view [] [settings-view])
(defmethod views :default [] [:div])

(defn show-view [view-name]
  [views view-name])

(defn main-view []

  (auth0/on-authenticated auth0/lock
                          config/auth0-del-opts-for-firebase
                          :auth0-authenticated
                          :auth0-error)
  (fb/on-auth-change fb/firebase-auth-object :firebase-auth-change)

  (let [users-db-path (fb/path-str->db-ref "users")]
    (fb/on-change "value" users-db-path :firebase-users-change))

  (let [active-view (rf/subscribe [:active-view])
        loading? (rf/subscribe [:set-loading-state?])
        src (rf/subscribe [:my-background-image-url])
        is-user-logged-in? (rf/subscribe [:my-id])]
    (fn []
      (set! (-> js/document .-title) @(rf/subscribe [:app-title]))
      (if (= @active-view :welcome-view)
        [show-view @active-view]
        [:div#main
         [lpsidebar-component]
         [:div#sidebar-wrap.hidden-sm-down
          [sidebar-component]]
         [:div.outer-height-wrap
          [search-bar]
          [:div.inner-height-wrap
             [:div.content {:style {:background-image (str "url(" @src ")")
                                    :background-size  "cover"}}
                [upload-image-component]
                [:button#change-header-btn
                 {:type     "button"
                  :class    "btn btn-secondary"
                  :style    {:font-size "1em"
                             :padding   "6px"
                             :display   (if @is-user-logged-in?
                                          "block"
                                          "none")}
                  :on-click #(rf/dispatch [:show-bg-img-upload true])}
                 [:i.fa.fa-pencil-square-o]]
                (when @loading?
                  [loading-component])
                [show-view @active-view]]]]]))))
