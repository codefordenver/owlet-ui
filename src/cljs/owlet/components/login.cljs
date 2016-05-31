(ns owlet.components.login
  (:require
    [reagent.session :as session]
    [cljsjs.auth0-lock :as Auth0Lock]
    [secretary.core :as secretary]
    [re-frame.core :as re-frame]))

(def lock (new js/Auth0Lock
               "aCHybcxZ3qE6nWta60psS0An1jHUlgMm"
               "codefordenver.auth0.com"))

(defn login-button []
      [:button.btn.btn-success.btn-sm
       {:type     "button"
        :on-click #(.show lock #js {:popup true}
                              (fn [err profile token]
                                  (if (not (nil? err))
                                    (.log js/console err)
                                    (do
                                      (re-frame/dispatch [:user-has-logged-in-out! true])
                                      (re-frame/dispatch [:update-social-id! (.-user_id profile)])
                                      ;; save the JWT token
                                      (.setItem js/localStorage "userToken" token)))))}
       "Log in"])

(defn logout-button []
      [:button.btn.btn-success.btn-sm
       {:type     "button"
        :on-click #(do
                    (re-frame/dispatch [:user-has-logged-in-out! false])
                    (session/remove! :user-id)
                    (.removeItem js/localStorage "userToken")
                    (secretary/dispatch! "/"))}
       "Log out"])

(defn login-component []
      (let [is-user-logged-in? (re-frame/subscribe [:is-user-logged-in?])]
           (fn []
               (if @is-user-logged-in?
                 [logout-button]
                 [login-button]))))
