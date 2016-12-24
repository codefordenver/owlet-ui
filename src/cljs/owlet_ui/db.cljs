(ns owlet-ui.db
  (:require [owlet-ui.config :as config]))

(def default-user-db
  "initial user state"
  {:logged-in?                false
   :social-id                 nil
   :content-entries           []
   :background-image          config/default-header-bg-image
   :background-image-entry-id nil})

(def default-db
  "initial app state"
  {:user                         default-user-db
   :app                          {:loading? nil}
   :activities                   []
   :activity-branches            nil
   :activities-by-branch-in-view nil
   :activities-by-branch         {}
   :activities-in-view           nil
   :activity-in-view             nil})
