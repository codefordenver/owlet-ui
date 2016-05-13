(ns owlet.app
  (:require
    [owlet.utils :refer [hydrate! get-user-cms-profile]]
    [owlet.views.settings :refer [settings-view]]
    [owlet.views.welcome :refer [welcome-view]]
    [owlet.components.header :refer [header-component]]
    [owlet.components.sidebar :refer [sidebar-component]]
    [reagent.core :as reagent :refer [atom]]
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

(defn main [child]
      (let [user-token (.getItem js/localStorage "userToken")
            user-content-types (atom {})
            get-header-content-type (fn [coll]
                                        (let [f (filterv #(= (get-in % [:sys :contentType :sys :id]) "userBgImage") coll)
                                              _ (println f)]
                                             (first f)))]
           (reagent/create-class
             {:component-did-mount
              (fn []
                  (when user-token
                        (.getProfile lock user-token
                                     (fn [err profile]
                                         (if-not (nil? err)
                                                 (let [user-id (.-user_id profile)]
                                                      (session/put! :user-id user-id)
                                                      (get-user-cms-profile user-id
                                                                            (fn [e]
                                                                                (hydrate! (:sid e) #(reset! user-content-types %))))
                                                      (session/put! :is-logged-in? true))))))
                  (.log js/console "did-mount"))
              :reagent-render
              (fn []
                  [:div#main
                   [sidebar-component (get-header-content-type @user-content-types)]
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
