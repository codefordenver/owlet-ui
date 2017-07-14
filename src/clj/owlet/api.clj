(ns owlet.api
  (:require [compojure.api.sweet :refer [context]]
            [compojure.core :refer [defroutes]]
            [owlet.routes.contentful :refer [routes]]))

(defroutes api-routes
           (context "/contentful" [] routes))