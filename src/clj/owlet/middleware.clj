(ns owlet.middleware
  (:require [owlet.env :refer [defaults]]
            [owlet.config :refer [env]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [owlet.layout :refer [*app-context* error-page]]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring-ttl-session.core :refer [ttl-memory-store]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]))

(defn wrap-csrf [handler]
  (wrap-anti-forgery
    handler
    {:error-response
     (error-page
       {:status 403
        :title "Invalid anti-forgery token"})}))

(defn wrap-formats [handler]
  (let [wrapped (wrap-restful-format
                  handler
                  {:formats [:json-kw :transit-json :transit-msgpack]})]
    (fn [request]
      ;; disable wrap-formats for websockets
      ;; since they're not compatible with this middleware
      ((if (:websocket? request) handler wrapped) request))))

(defn wrap-base [handler]
  (-> ((:middleware defaults) handler)
      wrap-json-response
      wrap-formats
      (wrap-defaults
        (-> site-defaults
            (assoc-in [:security :anti-forgery] false)
            (assoc-in  [:session :store] (ttl-memory-store (* 60 30)))))
      (wrap-cors :access-control-allow-origin [#".+"]
                 :access-control-allow-methods [:get :post :put :delete])))
