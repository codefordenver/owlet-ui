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
            [owlet-ui.views.branch-activities :refer [branch-activities-view]]
            [owlet-ui.async :as async]
            [owlet-ui.auth0 :as auth0]
            [owlet-ui.config :as config]
            [owlet-ui.firebase :as fb]))

(defmulti views identity)
(defmethod views :welcome-view [] [welcome-view])
(defmethod views :not-found-view [] [not-found-view])
(defmethod views :activity-view [] [activity-view])
(defmethod views :branches-view [] [branches-view])
(defmethod views :branch-activities-view [] [branch-activities-view])
(defmethod views :settings-view [] [settings-view])
(defmethod views :default [] [:div])

(defn show-view [view-name]
  [views view-name])

(def show? (reagent/atom false))

(defn main-view []

  (auth0/on-authenticated auth0/lock
                          config/auth0-del-opts-for-firebase
                          :auth0-authenticated
                          :auth0-error)
  (fb/on-auth-change fb/firebase-auth-object :firebase-auth-change)

  (let [users-db-path (fb/db-ref-for-path "users")]
    (fb/on-change users-db-path :firebase-users-change))
    ;(fb/change-on users-db-path :change-fb-users))
  ;(async/repeatedly-run)    ; Needed to poll subscriptions to update Firebase DB.

  (let [active-view (rf/subscribe [:active-view])
        loading? (rf/subscribe [:set-loading-state?])
        src (rf/subscribe [:user-has-background-image?])
        is-user-logged-in? (rf/subscribe [:my-user-id])
        open-modal (fn [] (reset! show? true))
        close-modal (fn [] (reset! show? false))]
    (fn []
      (set! (-> js/document .-title) @(rf/subscribe [:app-title]))
      (if (= @active-view :welcome-view)
        [show-view @active-view]
        [:div#main
         [:div#lpsidebar-overlay.hidden-md-up {:on-click #(js/closeSidebar)}]
         [:div#lpsidebar-wrap.hidden-md-up
          [lpsidebar-component]]
         [:img#lpsidebar-open.hidden-md-up {:src "img/owlet-tab-closed.png"
                                            :on-click #(js/openSidebar)}]
         [:img#lpsidebar-close.hidden-md-up {:src "img/owlet-tab-opened.png"
                                             :on-click #(js/closeSidebar)
                                             :style {:z-index "0"}}]
         [:div#sidebar-wrap.hidden-sm-down
          [sidebar-component]]
         [:div.outer-height-wrap
          [search-bar]
          [:div.inner-height-wrap
             [:div.content {:style {:background-image (str "url(" @src ")")
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
                [show-view @active-view]]]]]))))


(rf/reg-fx
  :set-local-storage
  (fn [clj-map]
    (doseq [[k v] clj-map]
      (.setItem js/localStorage (clj->js k) (clj->js v)))))


(rf/reg-cofx
  :get-local-storage
  (fn [cofx k]
    (.getItem js/localStorage (clj->js k))))

