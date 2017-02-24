(ns owlet-ui.db
  (:require [owlet-ui.config :as config]
            [owlet-ui.routes :refer [library-url]]))


(def default-db
  "initial app state"
  {:active-view                  nil
   :app                          {:loading?     nil
                                  :open-sidebar false
                                  :route-params {}
                                  :title        (str config/project-name " " "^OvO^")}
   :activities                   []
   :activity-branches            nil
   :activities-by-branch-in-view nil
   :activities-by-branch         {}
   :active-branch-activities     nil
   :my-identity                  nil
   :activity-in-view             nil
   :activity-titles              nil
   :activity-platforms           nil})

