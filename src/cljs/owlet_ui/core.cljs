(ns owlet-ui.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as rf]
              [devtools.core :as devtools]
              [dirac.runtime :as dirac]
              [owlet-ui.events]
              [owlet-ui.subs]
              [owlet-ui.routes :as routes]
              [owlet-ui.app :as app]
              [owlet-ui.config :as config]
              [re-frisk.core :refer [enable-re-frisk!]]))

(defn dev-setup []
  (when config/debug?
    (devtools/install!)
    (enable-console-print!)
    (enable-re-frisk!)
    (dirac/install!)))

(defn mount-root []
  (rf/clear-subscription-cache!)
  (reagent/render [app/main-view]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (rf/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root))
