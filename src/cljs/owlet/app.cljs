(ns ^:figwheel-always owlet.app
  (:require
    [owlet.utils :refer [hydrate! get-user-cms-profile]]
    [owlet.views.settings :refer [settings-view]]
    [owlet.views.welcome :refer [welcome-view]]
    [owlet.components.header :refer [header-component]]
    [owlet.components.sidebar :refer [sidebar-component]]
    [reagent.core :as reagent]
    [reagent.session :as session]
    [secretary.core :as secretary :include-macros true]
    [goog.events :as events]
    [goog.history.EventType :as HistoryEventType]
    [cljsjs.auth0-lock :as Auth0Lock])
  (:import goog.History))

(enable-console-print!)

(def lock (new js/Auth0Lock
               "aCHybcxZ3qE6nWta60psS0An1jHUlgMm"
               "codefordenver.auth0.com"))

(def app-state (reagent/atom {:user
                              {:is-user-logged-in? false}}))
#_(when user-token
        (do
          (println "got token")
          (.getProfile lock user-token
                       (fn [err profile]
                           (println "got here 1")
                           (if-not (nil? err)
                                   (let [user-id (.-user_id profile)]
                                        (session/put! :user-id user-id)
                                        (get-user-cms-profile user-id
                                                              (fn [e]
                                                                  (.log js/console e)
                                                                  (hydrate! (:sid e) #(reset! user-content-types %))))
                                        (session/put! :is-logged-in? true)))))
          [:p @user-content-types]))
;; (hydrate! "facebook|10156905787420019")

(defn main [child]
      (let [user-token (.getItem js/localStorage "userToken")
            user-content-types (reagent/atom {})]
           (reagent/create-class
             {:component-did-mount
              (fn []
                  (hydrate! "facebook|10156905787420019"))
              :reagent-render
              (fn []
                  [:div#main
                   [sidebar-component]
                   [:div.content
                    [header-component]
                    [child]]])})))

(def views
  {:main     (main welcome-view)
   :settings (main settings-view)})

(defn view []
      [(views (session/get :view))])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
                    (session/put! :view :main))

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
                  (let [url (.-token event)]
                       (if-not (= url "/")
                               (if (session/get :is-logged-in?)
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
      (mount-components))

(init!)
