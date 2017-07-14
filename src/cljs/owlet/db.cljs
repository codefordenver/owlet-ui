(ns owlet.db
  (:require [owlet.config :as config]))


(def default-db
  "initial app state"
  {:active-view                  nil
   :app                          {:loading?     nil
                                  :open-sidebar false
                                  :route-params {}
                                  :route-opts nil
                                  :title        (str config/project-name " " "^OvO^")}
   :activities                   []
   :activity-branches            nil
   :activities-by-filter nil
   :activities-by-branch         {}
   :active-branch-activities     nil
   :my-identity                  nil
   :activity-in-view             nil
   :activity-titles              nil
   :activity-platforms           nil})
