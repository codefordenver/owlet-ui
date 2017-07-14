(ns owlet.routes.home
  (:require [owlet.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]))

(defn index-page []
  (layout/render "index.html"))

(defroutes home-routes
  (GET "/" []
       (index-page))
  (GET "/docs" []
       (-> (response/ok (-> "docs/docs.md" io/resource slurp))
           (response/header "Content-Type" "text/plain; charset=utf-8"))))

