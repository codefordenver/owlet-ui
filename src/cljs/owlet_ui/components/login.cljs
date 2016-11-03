(ns owlet-ui.components.login
  (:require
    [owlet-ui.config :as config]
    [secretary.core :as secretary]
    [re-frame.core :as re-frame]))

(defn login-button []
      [:button.btn.btn-secondary.btn-sm
       {:type     "button"
        :on-click #(.show config/lock #js {:popup true})}
       "Log in"])

(defn logout-button []
      [:button.btn.btn-secondary.btn-sm
       {:type     "button"
        :on-click #(do
                    (re-frame/dispatch [:user-has-logged-in-out! false])
                    (.removeItem js/localStorage "userToken")
                    (secretary/dispatch! "/"))}
       "Log out"])

(defn login-component []
      (let [is-user-logged-in? (re-frame/subscribe [:is-user-logged-in?])]
           (fn []
               (if @is-user-logged-in?
                 [logout-button]
                 [login-button]))))
