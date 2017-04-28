(ns owlet-ui.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [re-frame.core :as rf]))


(defn app-routes []
  (secretary/set-config! :prefix "#")
  ;; --------------------
  ;; define routes here
  (defroute "/" []
            (rf/dispatch [:set-active-view :welcome-view]))

  (defroute "/404" []
            (rf/dispatch [:set-active-view :not-found-view]))

  (defroute "/about" []
            (rf/dispatch [:set-active-view :about-view]))

  (defroute "/settings" []
            (rf/dispatch [:set-active-view :settings-view]))

  (defroute "/branches" []
            (rf/dispatch [:get-library-content-from-contentful])
            (rf/dispatch [:set-active-view :branches-view])
            (rf/dispatch [:set-active-document-title! "Branches"]))

  (defroute "/search/:search" {:as params}
            (rf/dispatch [:set-active-view :search-results-view])
            (rf/dispatch [:get-library-content-from-contentful params]))

  (defroute "/:branch" {:as params}
            (rf/dispatch [:get-library-content-from-contentful params])
            (rf/dispatch [:set-active-view :branch-activities-view])
            (rf/dispatch [:set-active-document-title! (:branch params)]))

  (defroute "/activity/#!:activity" {:as params}
            (rf/dispatch [:get-library-content-from-contentful params])
            (rf/dispatch [:set-active-view :activity-view]))

  (defroute "*" []
            (let [uri (-> js/window .-location .-href)]
              (if (re-find #"%23" uri)
                (let [new-uri (js/decodeURIComponent uri)]
                  (set! (-> js/window .-location) new-uri))
                (set! (.-location js/window) "/#/404"))))

  ; Ensure browser history uses Secretary to dispatch.
  (doto (History.)
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))
