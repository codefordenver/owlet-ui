(ns owlet.utils
  (:require
    [ajax.core :refer [GET]]
    [reagent.session :as session]))

(defonce server-url "http://localhost:3000")                ;; "http://owlet-cms.apps.aterial.org"

(defn get-user-cms-profile [id & [cb]]
      (GET (str server-url "/api/user/" id)
           {:response-format :json
            :keywords?       true
            :handler         (fn [res]
                                 (session/put! :user-profile res)
                                 (when cb
                                       (cb res)))}))

(defn hydrate! [social-id & [cb]]
      (GET (str server-url "/api/content/get/entries?social-id=" social-id)
           {:response-format :json
            :keywords?       true
            :handler         (fn [res]
                                 (when cb
                                       (cb res)))}))