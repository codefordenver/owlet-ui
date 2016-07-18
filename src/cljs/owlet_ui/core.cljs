(ns owlet-ui.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [devtools.core :as devtools]
              [owlet-ui.handlers]
              [owlet-ui.subs]
              [owlet-ui.routes :as routes]
              [owlet-ui.app :as app]
              [owlet-ui.config :as config]))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")
    (devtools/install!)))

(defn mount-root []
  (reagent/render [app/main-view]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (re-frame/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root))
