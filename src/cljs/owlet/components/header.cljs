(ns owlet.components.header
  (:require
    [owlet.utils :refer [hydrate! CONTENTFUL-CREATE CONTENTFUL-UPDATE]]
    [owlet.components.login :refer [login-component]]
    [reagent.session :as session]
    [reagent.core :as reagent]
    [re-frame.core :as re-frame]))

(defn header-component []
      (let [user-bg-image (re-frame/subscribe [:user-has-background-image?])
            is-user-logged-in? (re-frame/subscribe [:is-user-logged-in?])]
           (reagent/create-class
             {:reagent-render
              (fn []
                  [:div#header
                   [:div.login
                    [login-component]]
                   (let [entry-id (get-in @user-bg-image [:sys :id])]
                        [:button#change-header-btn.btn-primary-outline.btn-sm
                         {:style {:display (if @is-user-logged-in?
                                             "block"
                                             "none")}
                          :on-click
                                 (fn []
                                     (let [url (js/prompt "i need a url")]
                                          (when url
                                                (if entry-id
                                                  (CONTENTFUL-UPDATE
                                                    "/api/content/update/entry"
                                                    {:params        {:content-type "userBgImage"
                                                                     :fields       {:url      {"en-US" url}
                                                                                    :socialid {"en-US" (session/get :user-id)}}
                                                                     :entry-id     entry-id}
                                                     :handler       (fn [res]
                                                                        (println res))
                                                     :error-handler (fn [err]
                                                                        (println err))})
                                                  (CONTENTFUL-CREATE
                                                    "/api/content/create/entry"
                                                    {:params        {:content-type  "userBgImage"
                                                                     :fields        {:url      {"en-US" url}
                                                                                     :socialid {"en-US" (session/get :user-id)}}
                                                                     :auto-publish? true}
                                                     :handler       (fn [res]
                                                                        (println res))
                                                     :error-handler (fn [err]
                                                                        (println err))})
                                                  ))))} "change me!"])
                   (let [src (get-in @user-bg-image [:fields :url :en-US])]
                        [:img {:src (or src "http://eskipaper.com/images/space-4.jpg")}])])})))
