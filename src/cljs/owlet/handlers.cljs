(ns owlet.handlers
  (:require [owlet.db :as db]
            [owlet.config :as config]
            [owlet.utils :refer [CONTENTFUL-CREATE CONTENTFUL-UPDATE]]
            [reagent.session :as session]
            [ajax.core :refer [GET]]
            [re-frame.core :as re-frame]))

(re-frame/register-handler
  :initialise-db!
  (fn [_ _]
      (assoc db/default-db :initialized? true)))

(re-frame/register-handler
  :user-has-logged-in-out!
  (re-frame/path [:user])                                   ;; path is midddleware
  (fn [db [_ val]]                                          ;; for traversing
      (assoc db :logged-in? val)))                          ;; init-app-state

(re-frame/register-handler
  :update-social-id!
  (re-frame/path [:user])
  (fn [db [_ sid]]
      (GET (str config/server-url "/api/content/get/entries?social-id=" sid)
           {:response-format :json
            :keywords?       true
            :handler         #(re-frame/dispatch [:process-fetch-entries-success! %1])
            :error-handler   #(println %)})
      (assoc db :social-id sid)))

(re-frame/register-handler
  :process-fetch-entries-success!
  (re-frame/path [:user :content-entries])
  (fn [db [_ entries]]
      (re-frame/dispatch [:set-user-background-image! entries])
      (conj db entries)))

(re-frame/register-handler
  :set-user-background-image!
  (re-frame/path [:user :background-image-entry])
  (fn [db [_ coll]]
      (let [filter-user-bg-image (fn [c]
                                     (filterv #(= (get-in % [:sys :contentType :sys :id])
                                                  "userBgImage") c))]
           (last (filter-user-bg-image coll)))))

(re-frame/register-handler
  :update-user-background!
  (fn [db [_ url]]
      (fn []
          #_(if
            (CONTENTFUL-UPDATE
              "/api/content/update/entry"
              {:params        {:content-type "userBgImage"
                               :fields       {:url      {"en-US" url}
                                              :socialid {"en-US" (session/get :user-id)}}
                               :entry-id     entry-id}
               :handler       (fn [res]
                                  (println res))
               :error-handler (fn [err]
                                  (println err))})
            (CONTENTFUL-CREATE
              "/api/content/create/entry"
              {:params        {:content-type  "userBgImage"
                               :fields        {:url      {"en-US" url}
                                               :socialid {"en-US" (session/get :user-id)}}
                               :auto-publish? true}
               :handler       (fn [res]
                                  (println res))
               :error-handler (fn [err]
                                  (println err))}))
          (identity db))))


