(ns owlet-ui.components.login
  (:require [owlet-ui.config :as config]
            [re-frame.core :as rf]))

(defn login-button []
  [:button.btn.btn-login.btn-sm
   {:type     "button"
    :on-click #(.show config/lock #js {:popup true})}
   "Log in"])

(defn logout-button []
  [:button.btn.btn-logout.btn-sm
   {:type     "button"
    :on-click #(do
                 (rf/dispatch [:user-has-logged-in-out! false])
                 (.removeItem js/localStorage "userToken"))}
   "Log out"])

(defn login-component []
  (if @(rf/subscribe [:is-user-logged-in?])
    [logout-button]
    [login-button]))

