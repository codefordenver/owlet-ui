(ns owlet.app
  (:require
    [owlet.views.settings :refer [settings-page]]
    [owlet.components.header :refer [header-component]]
    [owlet.components.login :refer [login-component]]
    [owlet.components.sidebar :refer [sidebar-component]]
    [reagent.core :as reagent :refer [atom]]
    [reagent.session :as session]
    [secretary.core :as secretary :include-macros true]
    [goog.events :as events]
    [goog.history.EventType :as HistoryEventType])
  (:import goog.History))

(enable-console-print!)

(defn main-page []
      [:div.no-gutter
       [sidebar-component]
       [:div.right.col-lg-10
        [header-component]
        [:div.search
         [login-component]
         [:input {
                  :type "search"
                  :name "sitesearch"}]
         [:input {:type  "submit"
                  :value "Search"}]]
        [:div.content]]])

(def pages
  {:main     #'main-page
   :settings #'settings-page})

(defn page []
      [(pages (session/get :page))])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
                    (session/put! :page :main))

(secretary/defroute "/settings" []
                    (session/put! :page :settings))
;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
      (doto (History.)
            (events/listen
              HistoryEventType/NAVIGATE
              (fn [event]
                  (secretary/dispatch! (.-token event))))
            (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-components []
      (reagent/render [#'page]
                      (.getElementById js/document "mount")))

(defn init! []
      (hook-browser-navigation!)
      (mount-components))

(init!)
