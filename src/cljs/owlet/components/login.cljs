(ns owlet.components.login
  (:require
    [owlet.utils :refer [get-user-cms-profile]]
    [reagent.core :refer [atom]]
    [reagent.session :as session]
    [ajax.core :refer [PUT]]
    [cljsjs.auth0-lock :as Auth0Lock]
    [secretary.core :as secretary]))

(def lock (new js/Auth0Lock
               "aCHybcxZ3qE6nWta60psS0An1jHUlgMm"
               "codefordenver.auth0.com"))

(defn login-component []
      (let [is-logged-in? (atom false)
            user-token (.getItem js/localStorage "userToken")
            _ (if user-token
                (.getProfile lock user-token
                             (fn [err profile]
                                 (if (not (nil? err))
                                   (.log js/console err)
                                   (do
                                     (session/put! :user-id (.-user_id profile))
                                     (get-user-cms-profile (.-user_id profile))
                                     (swap! is-logged-in? not)
                                     (session/put! :is-logged-in? true))))))]
           (fn []
               [:button.btn.btn-success.btn-sm
                {:type    "button"
                 :on-click #(if-not @is-logged-in?
                                   (.show lock #js {:popup true}
                                          (fn [err profile token]
                                              (if (not (nil? err))
                                                (.log js/console err)
                                                (do
                                                  (session/put! :user-id (.-user_id profile))
                                                  (session/put! :is-logged-in? true)
                                                  (swap! is-logged-in? not)
                                                  ;; save the JWT token
                                                  (.setItem js/localStorage "userToken" token)))))
                                   (do
                                     (swap! is-logged-in? not)
                                     (session/put! :is-logged-in? false)
                                     (session/remove! :user-id)
                                     (.removeItem js/localStorage "userToken")
                                     (secretary/dispatch! "/")))}
                (if @is-logged-in? "Log out"
                                   "Log in")])))
