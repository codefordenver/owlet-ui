(ns owlet.components.login
  (:require
    [owlet.utils :refer [get-user-cms-profile]]
    [reagent.core :refer [atom]]
    [reagent.session :as session]
    [ajax.core :refer [PUT]]
    [cljsjs.auth0-lock :as Auth0Lock]
    [secretary.core :as secretary]
    [re-frame.core :as re-frame]))

(def lock (new js/Auth0Lock
               "aCHybcxZ3qE6nWta60psS0An1jHUlgMm"
               "codefordenver.auth0.com"))

(defn login-component []
      (let [state (re-frame/subscribe [:is-user-logged-in?])]
           (fn []
               [:button.btn.btn-success.btn-sm
                {:type     "button"
                 :on-click (fn []
                               (if @state
                                 (do
                                   (re-frame/dispatch [:user-has-logged-in-out! false])
                                   (re-frame/dispatch [:update-social-id! nil])
                                   (session/remove! :user-id)
                                   (.removeItem js/localStorage "userToken")
                                   (secretary/dispatch! "/"))
                                 (.show lock #js {:popup true}
                                        (fn [err profile token]
                                            (if (not (nil? err))
                                              (.log js/console err)
                                              (do
                                                (re-frame/dispatch [:user-has-logged-in-out! true])
                                                (re-frame/dispatch [:update-social-id! (.-user_id profile)])
                                                ;; save the JWT token
                                                (.setItem js/localStorage "userToken" token)))))))}
                (if @state "Log out"
                           "Log in")])))
