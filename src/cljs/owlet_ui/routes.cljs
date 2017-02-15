(ns owlet-ui.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:require [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [re-frame.core :as re]
            [accountant.core :as accountant]
            [ajax.core :refer [GET]]
            [owlet-ui.config :as config])
  (:import goog.history.Html5History
           goog.Uri))


(accountant/configure-navigation!
 {:nav-handler  (fn [path]
                  (secretary/dispatch! path))
  :path-exists? (fn [path]
                  (secretary/locate-route path))})

(defn hook-browser-navigation! []
 (let [history (doto (Html5History.)
                 (events/listen
                   EventType/NAVIGATE
                   (fn [event]
                     (secretary/dispatch! (.-token event))))
                 (.setUseFragment false)
                 (.setPathPrefix "")
                 (.setEnabled true))]
   (events/listen js/document "click"
                  (fn [e]
                    (let [path (.getPath (.parse Uri (.-href (.-target e))))
                          title (.-title (.-target e))]
                      (when (secretary/locate-route path)
                        (. history (setToken path title))))))))


(def library-url
  "URL to obtain JSON containing the list of available activities.
  "
  (str config/server-url
       "/api/content/entries?library-view=true&space-id="
       config/owlet-activities-2-space-id))


(defn get-then-dispatch
  "Submits a GET request to the given URL and return immediately. The given
  callback function, response->event, will be called with the response body
  as argument, an EDN map.
  "
  [url response->event]
  (GET url {:response-format :json
            :keywords?       true
            :handler         (comp re/dispatch response->event)
            :error-handler   #(prn %)}))


(defn app-routes []
  ;; --------------------
  ;; define routes here
  (defroute "/" []
            (re/dispatch [:set-active-view :welcome-view]))

  (defroute "/404" []
            (re/dispatch [:set-active-view :not-found-view]))

  (defroute "/about" []
            (re/dispatch [:set-active-view :about-view]))

  (defroute "/settings" []
            (re/dispatch [:set-active-view :settings-view]))

  (defroute "/branches" []
            (re/dispatch [:get-library-content-from-contentful])
            (re/dispatch [:set-active-view :branches-view])
            (re/dispatch [:set-active-document-title! "Branches"]))

  (defroute "/search/:search" {:as params}
            (re/dispatch [:get-library-content-from-contentful params])
            (re/dispatch [:filter-activities-by-search-term (:search params)])
            (re/dispatch [:set-active-document-title! (:branch params)]))

  (defroute "/:branch" {:as params}
            (re/dispatch [:get-library-content-from-contentful params])
            (re/dispatch [:set-active-view :branch-activities-view])
            (re/dispatch [:set-active-document-title! (:branch params)]))

  (defroute "/activity/#!:activity" {:as params}
            (re/dispatch [:get-library-content-from-contentful params])
            (re/dispatch [:set-active-view :activity-view]))

  (defroute "*" []
            (set! (.-location js/window) "/404"))


  ;; --------------------
  (hook-browser-navigation!))
