(ns owlet-ui.components.header
  (:require
    [reagent.core :as reagent]
    [owlet-ui.components.login :refer [login-component]]
    [owlet-ui.components.upload-image-modal :refer [upload-image-component]]
    [re-frame.core :as re-frame]))

(def show? (reagent/atom false))

(defn header-component []
  (let [src (re-frame/subscribe [:user-has-background-image?])
        is-user-logged-in? (re-frame/subscribe [:is-user-logged-in?])
        open-modal (fn [_] (reset! show? true))
        close-modal (fn [_] (reset! show? false))]
    (fn []
      [:div#header
       [:div.login
        [login-component]]
       [upload-image-component show? close-modal]
       [:button#change-header-btn
        {:type     "button"
         :class    "btn btn-secondary"
         :style    {:font-size "1em"
                    :padding "6px"
                    :display (if @is-user-logged-in?
                               "block"
                               "none")}
         :on-click open-modal}
        ;<i class="fa fa-pencil-square-o" aria-hidden="true"></i>
        [:i.fa.fa-pencil-square-o]]
       [:img {:src @src}]])))
