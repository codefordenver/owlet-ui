(ns owlet.handlers
  (:require [re-frame.core :as re-frame]
            [owlet.db :as db]))

;; -- Event Handlers ----------------------------------------------------------

(re-frame/register-handler
  :user-has-logged-in-out!
  (re-frame/path [:user])                                   ;; path is midddleware
  (fn [db [_ val]]                                          ;; for traversing
      (assoc db :logged-in? val)))                          ;; init-app-state

(re-frame/register-handler
  :update-social-id!
  (re-frame/path [:user])
  (fn [db [_ sid]]
      (assoc db :social-id sid)))

(re-frame/register-handler
  :initialise-db!
  (fn [_ _]
      db/default-db))

(re-frame/register-handler
  :set-user-background-image!
  (re-frame/path [:user :background-image-entry])
  (fn [db [_ coll]]
      (let [filter-user-bg-image (fn [c]
                                     (filterv #(= (get-in % [:sys :contentType :sys :id])
                                                  "userBgImage") c))]
           (last (filter-user-bg-image coll)))))