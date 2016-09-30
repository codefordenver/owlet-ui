(ns owlet-ui.utils
  (:require
    [owlet-ui.config :as config]
    [ajax.core :refer [GET POST PUT]]
    [reagent.session :as session]))

(defn get-user-cms-profile [id & [cb]]
  (GET (str config/server-url "/api/user/" id)
       {:response-format :json
        :keywords?       true
        :handler         (fn [res]
                           (session/put! :user-profile res)
                           (when cb
                             (cb res)))}))
