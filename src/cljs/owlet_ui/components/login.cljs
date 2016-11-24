(ns owlet-ui.components.login
  (:require
    [owlet-ui.config :as config]
    [secretary.core :as secretary]
    [re-frame.core :as re-frame]))

(defn login-button []
  [:button.btn.btn-login.btn-sm
   {:type     "button"
    :on-click (fn [] (let [protocol (.-protocol js/location)
                           host (.-host js/location)
                           hash (.-hash js/location)
                           location (str protocol "//" host "/" hash)]
                       (.setItem js/localStorage "owlet.redirectUrl" location))
                (.show config/lock #js {:popup true}))}
   "Log in"])

(defn logout-button []
      [:button.btn.btn-logout.btn-sm
       {:type     "button"
        :on-click #(do
                    (re-frame/dispatch [:user-has-logged-in-out! false])
                    (.removeItem js/localStorage "userToken"))}
       "Log out"])

(defn login-component []
      (let [is-user-logged-in? (re-frame/subscribe [:is-user-logged-in?])]
           (fn []
               (if @is-user-logged-in?
                 [logout-button]
                 [login-button]))))
