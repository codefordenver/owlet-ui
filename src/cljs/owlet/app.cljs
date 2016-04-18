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
      [:div.container-fluid
       [:div.row.row-offcanvas.row-offcanvas-left
        [sidebar-component]
        [:div.col-md-9.col-lg-10
         [header-component]
         [:span.hidden-md-up
           [:button.btn-primary.btn-md {:type "button"
                                        :data-toggle "offcanvas"
                                        :onClick
                                          (fn []
                                            (-> (js/$ ".row-offcanvas")
                                              (.toggleClass "active")))} "Menu"]]
         [:div.login
          [login-component]]
         [:div.search.pull-right
          [:input {:type "search"
                   :name "sitesearch"}]
          [:input {:type "submit"
                   :value "\uD83D\uDD0D"}]]]]]])

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
