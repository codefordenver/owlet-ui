(ns ^:figwheel-always owlet.app
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require
    [owlet.utils :refer [hydrate! get-user-cms-profile]]
    [owlet.views.settings :refer [settings-view]]
    [owlet.views.welcome :refer [welcome-view]]
    [owlet.views.library :refer [library-view]]
    [owlet.components.header :refer [header-component]]
    [owlet.components.sidebar :refer [sidebar-component]]
    [reagent.core :as reagent]
    [reagent.session :as session]
    [secretary.core :as secretary :include-macros true]
    [goog.events :as events]
    [goog.history.EventType :as HistoryEventType]
    [cljsjs.auth0-lock :as Auth0Lock]
    [re-frame.core :as re-frame])
  (:import goog.History))

(enable-console-print!)

(def init-app-state
  {:user {:logged-in?       false
          :social-id        nil
          :content-entries  []
          :background-image nil}})

(def lock (new js/Auth0Lock
               "aCHybcxZ3qE6nWta60psS0An1jHUlgMm"
               "codefordenver.auth0.com"))

;; -- Event Handlers ----------------------------------------------------------

(re-frame/register-handler
  :user-has-logged-in-out!
  (re-frame/path [:user])                                   ;; path is midddleware
  (fn [db [_ val]]                                          ;; for traversing
      (assoc db :logged-in? val)))                          ;; init-app-state

(re-frame/register-handler
  :update-social-id!
  (re-frame/path [:user])
  (fn [db [_ val]]
      (let []
           (hydrate! val #(re-frame/dispatch-sync [:set-user-background-image! %]))
           (assoc db :social-id val))))

(re-frame/register-handler
  :initialise-db!
  (fn [db _]
      (merge db init-app-state)))

(re-frame/register-handler
  :set-user-background-image!
  (re-frame/path [:user :background-image])
  (fn [db [_ coll]]
      (let [filter-user-bg-image (fn [c]
                                     (filterv #(= (get-in % [:sys :contentType :sys :id])
                                                  "userBgImage") c))]
           (-> (filter-user-bg-image coll)
               last
               (get-in [:fields :url])))))

;; -- Subscription Handlers ---------------------------------------------------

(re-frame/register-sub
  :is-user-logged-in?
  (fn [db]
      (reaction (get-in @db [:user :logged-in?]))))

(re-frame/register-sub
  :social-id-subscription
  (fn [db]
      (reaction (get-in @db [:user :social-id]))))

(re-frame/register-sub
  :user-has-background-image?
  (fn [db]
      (reaction (get-in @db [:user :background-image]))))

;; -- Reagent/React Componentry -----------------------------------------------

(defn main [child]
      (let [user-token (.getItem js/localStorage "userToken")
            user-logged-in? (re-frame/subscribe [:is-user-logged-in?])]
           (reagent/create-class
             {:component-will-mount
              (fn []
                  (if (and user-token (not @user-logged-in?))
                    (.getProfile lock user-token
                                 (fn [err profile]
                                     (if (not (nil? err))
                                       (.log js/console err)
                                       (do
                                         (re-frame/dispatch [:user-has-logged-in-out! true])
                                         (re-frame/dispatch [:update-social-id! (.-user_id profile)])
                                         (session/put! :user-id (.-user_id profile))))))))
              :reagent-render
              (fn []
                  [:div#main
                   [sidebar-component]
                   [:div.content
                    [header-component]
                    [child]]])})))

(def views
  {:main     (main welcome-view)
   :library  (main library-view)
   :settings (main settings-view)})

(defn view []
      [(views (session/get :view))])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
                    (session/put! :view :main))

(secretary/defroute "/library" []
                    (session/put! :view :library))

(secretary/defroute "/settings" []
                    (session/put! :view :settings))
;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
      (doto (History.)
            (events/listen
              HistoryEventType/NAVIGATE
              (fn [event]
                  (let [url (.-token event)
                        user-logged-in? (re-frame/subscribe [:is-user-logged-in?])]
                       (if-not (= url "/")
                               (if @user-logged-in?
                                 (secretary/dispatch! url)
                                 (secretary/dispatch! "/"))
                               (secretary/dispatch! url)))))
            (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-components []
      (reagent/render [#'view]
                      (.getElementById js/document "mount")))

(defn init! []
      (hook-browser-navigation!)
      (re-frame/dispatch [:initialise-db!])
      (mount-components))

(init!)
