(ns owlet.app
  (:require
    [owlet.views.settings :refer [settings-page]]
    [owlet.components.header :refer [header-component]]
    [owlet.components.sidebar :refer [sidebar-component]]
    [reagent.core :as reagent :refer [atom]]
    [reagent.session :as session]
    [secretary.core :as secretary :include-macros true]
    [goog.events :as events]
    [goog.history.EventType :as HistoryEventType])
  (:import goog.History))

(enable-console-print!)

(defn main-page []
      [:div.jumbotron
       ;; TODO: refactor search into own component
       [:div.search.pull-right
        [:input {:type "search"
                 :name "sitesearch"}]
        [:input {:type "submit"
                 :value "\uD83D\uDD0D"}]]
       [:div.container-fluid
        [:div.row
         [:div.col-lg-12
          [:p.text-center
           "main content area"]]]]])

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
      (reagent/render [#'sidebar-component]
                      (.getElementById js/document "sidebar"))
      (reagent/render [#'header-component]
                      (.getElementById js/document "header"))
      (reagent/render [#'page]
                      (.getElementById js/document "main")))

(defn init! []
      (hook-browser-navigation!)
      (mount-components))

(init!)
