(ns owlet.main
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]
            [owlet.components.upload-image-modal :refer [upload-image-component]]
            [owlet.components.sidebar :refer [sidebar-component]]
            [owlet.components.lpsidebar :refer [lpsidebar-component]]
            [owlet.components.loading :refer [loading-component]]
            [owlet.views.welcome :refer [welcome-view]]
            [owlet.components.search-bar :refer [search-bar]]
            [owlet.views.not-found :refer [not-found-view]]
            [owlet.views.activity :refer [activity-view]]
            [owlet.views.branches :refer [branches-view]]
            [owlet.views.subscribed :refer [subscribed-view]]
            [owlet.views.unsubscribe :refer [unsubscribe-view]]
            [owlet.views.settings :refer [settings-view]]
            [owlet.views.filtered-activities :refer [filtered-activities-view]]
            [owlet.async :as async]
            [owlet.auth0 :as auth0]
            [owlet.config :as config]
            [owlet.firebase :as fb]))

(defmulti views identity)
(defmethod views :welcome-view [] [welcome-view])
(defmethod views :filtered-activities-view []
  [filtered-activities-view (first (keys @(rf/subscribe [:route-params])))])
(defmethod views :not-found-view [] [not-found-view])
(defmethod views :activity-view [] [activity-view])
(defmethod views :branches-view [] [branches-view])
(defmethod views :subscribed-view [] [subscribed-view])
(defmethod views :unsubscribe-view [] [unsubscribe-view])
(defmethod views :settings-view [] [settings-view])
(defmethod views :default [] [:div])

(defn show-view [view-name]
  [views view-name])

(defn view []

  (auth0/on-authenticated auth0/lock
                          config/auth0-del-opts-for-firebase
                          :auth0-authenticated
                          :auth0-error)
  (fb/on-auth-change fb/firebase-auth-object :firebase-auth-change)

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
