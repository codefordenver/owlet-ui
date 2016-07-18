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
                (let [entry-id (get-in @user-bg-image [:sys :id])]

                     [:button#change-header-btn.btn-primary-outline.btn-sm
                      {:type     "button"
                       :style    {:display (if @is-user-logged-in?
                                             "block"
                                             "none")}
                        :on-click (fn [e]
                                    (.preventDefault e)
                                    (let [url (js/prompt "i need a url")]
                                      (when url
                                        (re-frame/dispatch [:update-user-background! url]))))}


                      "change me!"])
                (let [src (get-in @user-bg-image [:fields :url :en-US])]
                     [:img {:src src}])])))
