(ns owlet-ui.components.header
  (:require
    [owlet-ui.components.login :refer [login-component]]
    [re-frame.core :as re-frame]))

(defn header-component []
  (let [user-bg-image (re-frame/subscribe [:user-has-background-image?])
        is-user-logged-in? (re-frame/subscribe [:is-user-logged-in?])]
    (fn []
      [:div#header
       [:div.login
        [login-component]]
       [:button#change-header-btn.btn-primary-outline.btn-sm
        {:type     "button"
         :style    {:display (if @is-user-logged-in?
                               "block"
                               "none")}
         :on-click (fn [_]
                     (let [url (js/prompt "i need a url")]
                       (when url
                         (re-frame/dispatch [:update-user-background! url]))))}


        "change me!"]
       [:img {:src @user-bg-image}]])))
