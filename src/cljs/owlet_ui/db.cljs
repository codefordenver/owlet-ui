(ns owlet-ui.db
  (:require [owlet-ui.config :as config]))

(def default-db
  "init app-state"
  {:user {:logged-in?       false
          :social-id        nil
          :content-entries  []
          :background-image config/default-header-bg-image
          :background-image-entry-id nil
          :background-image-entry-version 0
          }
   :app  {:initialized? false}})
