(ns owlet-ui.components.header
  (:require
    [owlet-ui.components.login :refer [login-component]]
    [owlet-ui.firebase :as firebase]
    [re-frame.core :as re-frame]))

(defn handle-firebase-upload [element-id]
  (let [el (.getElementById js/document element-id)
        file (aget (.-files el) 0)]
    (firebase/upload-file file)))

(defn updload-button []
  [:button {:class    "btn btn-primary" :type "button"
            :on-click #(handle-firebase-upload "upload-file")}
   "upload" [:span {:class "fa fa-upload"}]])

(defn upload-component []
  [:div
   [:form#change-header-btn.btn-primary-outline.btn-sm
    [:label "Upload Filename: "]
    [:input#upload-file
     {:type "file"
      :name "upload-file"}]
    [updload-button]]])

(defn header-component []
  (let [src (re-frame/subscribe [:user-has-background-image?])
        is-user-logged-in? (re-frame/subscribe [:is-user-logged-in?])]
    (fn []
      [:div#header
       [:div.login
        [login-component]]
       [:button#change-header-btn.btn.btn-outline-secondary.btn-sm]
       (when @is-user-logged-in?
         [upload-component])
       [:button#change-header-btn.btn-primary-outline.btn-sm
        {:type     "button"
         :style    {:display (if @is-user-logged-in?
                               "block"
                               "none")}
         :on-click (fn [_])}
                     ;(let [url (js/prompt "i need a url")]
                     ;  (when url
                     ;    (re-frame/dispatch [:update-user-background! url])])}


        "change me!"]
       [:img {:src @src}]])))
