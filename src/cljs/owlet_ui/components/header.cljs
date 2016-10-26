(ns owlet-ui.components.header
  (:require
    [reagent.core :as reagent]
    [owlet-ui.components.login :refer [login-component]]
    [owlet-ui.components.upload-image-modal :refer [upload-image-component]]
    [re-frame.core :as re-frame]))

(defn header-component []
  (let [src (re-frame/subscribe [:user-has-background-image?])
        is-user-logged-in? (re-frame/subscribe [:is-user-logged-in?])
        show-upload? (reagent/atom false)]
    (fn []
      [:div#header
       [:div.login
        [login-component]]
       [upload-image-component show-upload?]
       [:button#change-header-btn.btn.btn-outline-secondary.btn-sm
        {:type     "button"
         :style    {:display (if @is-user-logged-in?
                               "block"
                               "none")}
         :on-click #(reset! show-upload? true)}
        "change header"]
       [:img {:src @src}]])))
