(ns owlet-ui.components.login
  (:require
    [owlet-ui.config :as config]
    [secretary.core :as secretary]
    [re-frame.core :as re-frame]))

(defn login-button []
      [:button.btn.btn-success.btn-sm
       {:type     "button"
        :on-click #(.show config/lock #js {:popup true}
                          (fn [err profile token]
                              (if (not (nil? err))
                                (prn err)
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
                    (.removeItem js/localStorage "userToken")
                    (secretary/dispatch! "/"))}
       "Log out"])

(defn login-component []
      (let [is-user-logged-in? (re-frame/subscribe [:is-user-logged-in?])]
           (fn []
               (if @is-user-logged-in?
                 [logout-button]
                 [login-button]))))
