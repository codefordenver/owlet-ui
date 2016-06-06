(ns owlet.app
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require
    [owlet.views.settings :refer [settings-view]]
    [owlet.views.welcome :refer [welcome-view]]
    [owlet.views.library :refer [library-view]]
    [owlet.config :as config]
    [owlet.handlers]
    [owlet.subs]
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

(when config/debug?
      (println "__dev-mode__"))

(def lock (new js/Auth0Lock
               "aCHybcxZ3qE6nWta60psS0An1jHUlgMm"
               "codefordenver.auth0.com"))

;; -- Reagent/React Componentry -----------------------------------------------

(defn main [child]
      (let [user-token (.getItem js/localStorage "userToken")
            user-logged-in? (re-frame/subscribe [:is-user-logged-in?])
            sid (session/get :user-id)]
           (reagent/create-class
             {:component-did-mount
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

;; -- Routes -----------------------------------------------

(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
                    (session/put! :view :main))

(secretary/defroute "/library" []
                    (session/put! :view :library))

(secretary/defroute "/settings" []
                    (session/put! :view :settings))

;; -- History ----------------------------------------------

(defn hook-browser-navigation!
      "must be called after routes have been defined"
      []
      (doto (History.)
            (events/listen
              HistoryEventType/NAVIGATE
              (fn [event]
                  (let [url (.-token event)
                        user-logged-in? (re-frame/subscribe [:is-user-logged-in?])]
                       (if-not (= url "/")
                               (if @user-logged-in?
                                 (secretary/dispatch! url)
                                 (secretary/dispatch! "#/"))
                               (secretary/dispatch! url)))))
            (.setEnabled true)))

;; -- Init App ---------------------------------------------

(defn mount-components []
      (reagent/render [#'view]
                      (.getElementById js/document "mount")))

(defn init []
      ;(when-not @(re-frame/subscribe [:initialized?])
      ;          (re-frame/dispatch [:initialise-db!]))
      (hook-browser-navigation!)
      (re-frame/dispatch [:initialise-db!])
      (mount-components))

(init)
