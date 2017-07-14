(ns owlet.components.login
  (:require [re-frame.core :as rf]
            [owlet.auth0 :as auth0]
            [owlet.firebase :as fb]))


(defn login-button
  []
  [:button.btn.btn-login.btn-sm
   {:type     "button"
    :on-click #(.show auth0/lock)}
   "Log in"])


(defn logout-button
  []
  [:button.btn.btn-logout.btn-sm
   {:type     "button"
    :on-click #(rf/dispatch [:log-out])}
   "Log out"])


(defn login-component
  []
  (if @(rf/subscribe [:my-id])
    [logout-button]
    [login-button]))

