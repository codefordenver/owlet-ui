(ns owlet-ui.routes
    (:require-macros [secretary.core :refer [defroute]])
    (:import goog.History)
  (:require [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [re-frame.core :as re]
            [ajax.core :refer [GET]]
            [owlet-ui.config :as config]))


(def ^:private library-url
  "URL to obtain JSON containing the list of available activities.
  "
  (str config/server-url
       "/api/content/entries?library-view=true&space-id="
       config/library-space-id))


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
  (secretary/set-config! :prefix "#")
  ;; --------------------
  ;; define routes here
  (defroute "/" []
            (re/dispatch [:set-active-view :welcome-view]))

  (defroute "/about" []
            (re/dispatch [:set-active-view :about-view]))

  (defroute "/library" []
            ; Before dispatching to the view, ensure we have current activity library.
            (get-then-dispatch library-url #(vector :activities-get-successful %))
            (re/dispatch [:set-active-view :library-view]))

  (defroute "/settings" []
            (re/dispatch [:set-active-view :settings-view]))

  ; Ensure browser history uses Secretary to dispatch.
  (doto (History.)
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

