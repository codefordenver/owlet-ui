(ns owlet-ui.components.header
  (:require
    [owlet-ui.components.login :refer [login-component]]
    [owlet-ui.components.upload-image-modal :refer [upload-image-component]]
    [re-frame.core :as re-frame]))

(defn header-component []
  (let [src (re-frame/subscribe [:user-has-background-image?])
        is-user-logged-in? (re-frame/subscribe [:is-user-logged-in?])]
    (fn []
      [:div#header
       [:div.login
        [login-component]]
       [upload-image-component true]
       [:button#change-header-btn.btn.btn-outline-secondary.btn-sm]
       ;(when @is-user-logged-in?
       ;  [upload-component]]
       [:button#change-header-btn.btn-primary-outline.btn-sm
        {:type     "button"
         :style    {:display (if @is-user-logged-in?
                               "block"
                               "none")}
         :on-click (fn [_])}
                     ;; TODO: add show modal logic
        "change me!"]
       [:img {:src @src}]])))
