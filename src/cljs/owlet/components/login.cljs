(ns owlet.components.login
  (:require [reagent.core :refer [atom]]
            [reagent.session :as session]
            [ajax.core :refer [GET PUT]]
            [cljsjs.auth0-lock :as Auth0Lock]))

(defonce server-url "http://localhost:3000")                ;; "http://owlet-cms.apps.aterial.org"

(def user-profile (atom nil))

(def lock (new js/Auth0Lock
               "aCHybcxZ3qE6nWta60psS0An1jHUlgMm"
               "codefordenver.auth0.com"))

(defn get-user-cms-profile [id]
      (GET (str server-url "/api/user/" id)
           {:response-format :json
            :handler         (fn [res]
                                 (reset! user-profile (clj->js res)))}))

(defn login-component []
      (let [is-logged-in? (atom false)
            user-token (.getItem js/localStorage "userToken")
            _ (if user-token
                (.getProfile lock user-token
                             (fn [err profile]
                                 (if (not (nil? err))
                                   (.log js/console err)
                                   (do
                                     (get-user-cms-profile (.-user_id profile))
                                     (swap! is-logged-in? not)
                                     (session/put! :user-id (.-user_id profile)))))))]
           (fn []
               [:button.btn.btn-success.btn-sm
                 {:type    "button"
                  :onClick #(if-not @is-logged-in?
                                    (.show lock #js {:popup true}
                                           (fn [err profile token]
                                               (if (not (nil? err))
                                                 (print err)
                                                 (do
                                                   (session/put! :user-id (.-user_id profile))
                                                   (.log js/console profile)
                                                   (swap! is-logged-in? not)
                                                   ;; save the JWT token
                                                   (.setItem js/localStorage "userToken" token)))))
                                    (do
                                      (swap! is-logged-in? not)
                                      (session/remove! :user-id)
                                      (.removeItem js/localStorage "userToken")))}
                 (if @is-logged-in? "Log out"
                                    "Log in")])))
