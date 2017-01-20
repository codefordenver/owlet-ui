(ns owlet-ui.db
  (:require [owlet-ui.config :as config]
            [owlet-ui.routes :refer [library-url]]))

(def default-user-db
  "initial user state"
  {:logged-in?                false
   :social-id                 nil
   :content-entries           []
   :background-image          config/default-header-bg-image
   :background-image-entry-id nil})

(def default-db
  "initial app state"
  {:active-view                  nil
   :user                         default-user-db
   :app                          {:loading?     nil
                                  :open-sidebar false
                                  :route-params {}
                                  :title        (str config/project-name " " "^OvO^")}
   :activities                   []
   :activity-branches            nil
   :activities-by-branch-in-view nil
   :activities-by-branch         {}
   :active-branch-activities     nil
   :id                           nil
   :activity-in-view             nil})
