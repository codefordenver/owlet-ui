(ns owlet-ui.app
  (:require [re-frame.core :as re]
            [reagent.core :as reagent]
            [owlet-ui.components.upload-image-modal :refer [upload-image-component]]
            [owlet-ui.components.sidebar :refer [sidebar-component]]
            [owlet-ui.components.lpsidebar :refer [lpsidebar-component]]
            [owlet-ui.components.loading :refer [loading-component]]
            [owlet-ui.views.welcome :refer [welcome-view]]
            [owlet-ui.views.activity :refer [activity-view]]
            [owlet-ui.views.branches :refer [branches-view]]
            [owlet-ui.views.settings :refer [settings-view]]
            [owlet-ui.views.branch-activities :refer [branch-activities-view]]
            [owlet-ui.async :as async]
            [owlet-ui.auth0 :as auth0]
            [owlet-ui.firebase :as fb]))

(defmulti views identity)
(defmethod views :welcome-view [] [welcome-view])
(defmethod views :activity-view [] [activity-view])
(defmethod views :branches-view [] [branches-view])
(defmethod views :branch-activities-view [] [branch-activities-view])
(defmethod views :settings-view [] [settings-view])
(defmethod views :default [] [:div])

(defn show-view [view-name]
  [views view-name])

(def show? (reagent/atom false))

(defn main-view []
  (async/repeatedly-run)    ; Needed to poll subscriptions to update Firebase DB.

  (auth0/on-authenticated auth0/lock :authenticated)
  (fb/on-auth-change fb/firebase-auth-object :firebase-user)

  (let [users-db-path (fb/db-ref-for-path "users")]
    (fb/on-change users-db-path :fb-users-change)
    (fb/change-on users-db-path :change-fb-users))

  (let [active-view (re/subscribe [:active-view])
        loading? (re/subscribe [:set-loading-state?])
        src (re/subscribe [:user-has-background-image?])
        is-user-logged-in? (re/subscribe [:my-user-id])
        open-modal (fn [] (reset! show? true))
        close-modal (fn [] (reset! show? false))
        open-sidebar? (re/subscribe [:open-sidebar?])]
    (reagent/create-class
      {:component-will-mount
       #(re/dispatch [:get-auth0-profile])
       :reagent-render
       (fn []
         (set! (-> js/document .-title) @(re/subscribe [:app-title]))
         (if (= @active-view :welcome-view)
           [show-view @active-view]
           [:div#main
            [:div#lpsidebar-wrap.hidden-md-up
             [lpsidebar-component]]
            [:input#lpsidebar-trigger.hidden-md-up
             {:type      "checkbox"
              :on-change #(re/dispatch [:set-sidebar-state! (not @open-sidebar?)])
              :checked   @(re/subscribe [:open-sidebar?])}]
            [:label.hidden-md-up {:for      "lpsidebar-trigger"
                                  :on-click #(re/dispatch [:set-sidebar-state! (not @open-sidebar?)])}]
            [:div#sidebar-wrap.hidden-sm-down
             [sidebar-component]]
            [:div.content {:style {:width            "100%"
                                   :background-image (str "url(" @src ")")
                                   :background-size  "cover"}}
             [upload-image-component show? close-modal]
             [:button#change-header-btn
              {:type     "button"
               :class    "btn btn-secondary"
               :style    {:font-size "1em"
                          :padding   "6px"
                          :display   (if @is-user-logged-in?
                                       "block"
                                       "none")}
               :on-click open-modal}
              [:i.fa.fa-pencil-square-o]]
             (when @loading?
               [loading-component])
             [show-view @active-view]]]))})))
