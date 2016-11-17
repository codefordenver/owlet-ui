(ns owlet-ui.components.login
  (:require
    [owlet-ui.auth0 :as auth0]
    [re-frame.core :as re]
    [owlet-ui.firebase :as fb]))

(defn login-button []
      [:button.btn.btn-login.btn-sm
       {:type     "button"
        :on-click #(.show auth0/lock #js {:popup true})}
       "Log in"])

(defn logout-button []
      [:button.btn.btn-logout.btn-sm
       {:type     "button"
        :on-click #(do (.signOut fb/firebase-auth-object)
                       (re/dispatch [:user-has-logged-in-out! false]))}
       "Log out"])

(defn login-component []
      (let [is-user-logged-in? (re/subscribe [:my-user-id])]
           (fn []
               (if @is-user-logged-in?
                 [logout-button]
                 [login-button]))))
