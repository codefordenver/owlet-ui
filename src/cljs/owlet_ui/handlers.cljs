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
  (re-frame/path [:user])
  (fn [db [_ val]]
    ;; reset user-bg-image on logout
    (when (false? val)
      (do
        (re-frame/dispatch [:reset-user-bg-image! config/default-header-bg-image])
        (re-frame/dispatch [:reset-user-db!])))
    (assoc db :logged-in? val)))

(re-frame/register-handler
  :reset-user-db!
  (fn [_ [_ _]]
    db/default-db))

(re-frame/register-handler
  :update-social-id!
  (re-frame/path [:user])
  (fn [db [_ sid]]
    (GET (str config/server-url "/api/content/entries?social-id=" sid)
         {:response-format :json
          :keywords?       true
          :handler         #(re-frame/dispatch [:process-fetch-entries-success! %1])
          :error-handler   #(prn %)})
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
                                              "userBgImage") c))
          user-bg-image-entries (last (filter-user-bg-image coll))
          entry-id (get-in user-bg-image-entries [:sys :id])]
      ;; set :background-image-entry-id
      (re-frame/dispatch [:set-backround-image-entry-id! entry-id])
      (get-in user-bg-image-entries [:fields :url :en-US]))))

(re-frame/register-handler
  :set-backround-image-entry-id!
  (re-frame/path [:user :background-image-entry-id])
  (fn [_ [_ id]]
    id))

(re-frame/register-handler
  :update-user-background!
  (fn [db [_ url]]
    ;; if we have a url and an entry-id, aka existing entry for *userBgImage*
    ;; perform an update
    (let [entry-id (get-in db [:user :background-image-entry-id])]
      (if (and url entry-id)
        (PUT
          (str config/server-url "/api/content/entries")
          {:response-format :json
           :keywords?       true
           :params          {:content-type "userBgImage"
                             :fields       {:url      {"en-US" url}
                                            :socialid {"en-US" (get-in db [:user :social-id])}}
                             :entry-id     entry-id}
           :handler         #(re-frame/dispatch [:update-user-background-after-successful-post! %1])
           :error-handler   #(prn %)})
        (POST
          (str config/server-url "/api/content/entries")
          {:response-format :json
           :keywords?       true
           :params          {:content-type  "userBgImage"
                             :fields        {:url      {"en-US" url}
                                             :socialid {"en-US" (get-in db [:user :social-id])}}
                             :auto-publish? true}
           :handler         #(re-frame/dispatch [:update-user-background-after-successful-post! %1])
           :error-handler   #(prn %)})))
    db))

(re-frame/register-handler
  :update-user-background-after-successful-post!
  (re-frame/path [:user :background-image])
  (fn [_ [_ res]]
    (re-frame/dispatch [:set-backround-image-entry-id! (get-in res [:sys :id])])
    (get-in res [:fields :url :en-US])))

(re-frame/register-handler
  :reset-user-bg-image!
  (re-frame/path [:user :background-image])
  (fn [_ [_ url]]
    url))

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