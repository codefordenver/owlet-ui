(ns owlet-ui.components.login
  (:require [re-frame.core :as rf]
            [owlet-ui.auth0 :as auth0]
            [owlet-ui.firebase :as fb]))


(defn login-button
  []
  [:button.btn.btn-login.btn-sm
   {:type     "button"
    :on-click #(.show auth0/lock #js {:popup true})}
   "Log in"])


(defn logout-button
  []
  [:button.btn.btn-logout.btn-sm
   {:type     "button"
    :on-click #(do (.signOut fb/firebase-auth-object)
                   (rf/dispatch [:user-has-logged-in-out! false]))}
   "Log out"])


(defn login-component
  []
  (if @(rf/subscribe [:is-user-logged-in?])
    [logout-button]
    [login-button]))

