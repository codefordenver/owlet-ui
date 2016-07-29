(ns owlet-ui.handlers
  (:require [re-frame.core :as re-frame]
            [owlet-ui.db :as db]
            [owlet-ui.config :as config]
            [ajax.core :refer [GET POST PUT]]))

(re-frame/register-handler
  :initialize-db
  (fn [_ _]
    db/default-db))

(re-frame/register-handler
  :set-active-view
  (fn [db [_ active-view]]
    (assoc db :active-view active-view)))

(re-frame/register-handler
  :user-has-logged-in-out!
  (re-frame/path [:user])                                   ;; path is midddleware
  (fn [db [_ val]]                                          ;; for traversing
    (assoc db :logged-in? val)))                            ;; init-app-state

(re-frame/register-handler
  :update-social-id!
  (re-frame/path [:user])
  (fn [db [_ sid]]
    (GET (str config/server-url "/api/content/entries?social-id=" sid)
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
  (re-frame/path [:user :background-image])
  (fn [db [_ coll]]
    (let [filter-user-bg-image (fn [c]
                                 (filterv #(= (get-in % [:sys :contentType :sys :id])
                                              "userBgImage") c))]
      (get-in (last (filter-user-bg-image coll)) [:fields :url :en-US]))))

(re-frame/register-handler
  :update-user-background!
  (fn [db [_ url entry-id]]
    (if url
      (POST
        (str config/server-url "/api/content/entries")
        {:response-format :json
         :keywords?       true
         :params        {:content-type  "userBgImage"
                         :fields        {:url      {"en-US" url}
                                         :socialid {"en-US" (get-in db [:user :social-id])}}
                         :auto-publish? true}
         :handler       (fn [res]
                          (re-frame/dispatch [:update-user-background-after-success-post! res]))
         :error-handler #(prn %)}))
    db))

(re-frame/register-handler
  :update-user-background-after-success-post!
  (re-frame/path [:user :background-image])
  (fn [_ [_ res]]
    (get-in res [:fields :url :en-US])))

(re-frame/register-handler
  :get-auth0-profile
  (fn [db [_ _]]
    (let [user-token (.getItem js/localStorage "userToken")]
      (.getProfile config/lock user-token
                   (fn [err profile]
                     (if (not (nil? err))
                       (prn err)
                       (do
                         (re-frame/dispatch [:user-has-logged-in-out! true])
                         (re-frame/dispatch [:update-social-id! (.-user_id profile)])))))
      db)))