(ns owlet-ui.components.header
  (:require
    [owlet-ui.components.login :refer [login-component]]
    [re-frame.core :as re-frame]))

(defn header-component []
  (let [src (re-frame/subscribe [:user-has-background-image?])
        is-user-logged-in? (re-frame/subscribe [:is-user-logged-in?])]
    (fn []
      [:div#header
       [:div.login
        [login-component]]
       [:button#change-header-btn.btn.btn-outline-secondary.btn-sm
        {:type     "button"
         :style    {:display (if @is-user-logged-in?
                               "block"
                               "none")}
         :on-click (fn [_]
                     (let [url (js/prompt "enter image url")]
                       (when url
                         (re-frame/dispatch [:update-user-background! url]))))}
        "change header"]
       [:img {:src @src}]])))
