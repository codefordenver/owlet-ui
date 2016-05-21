(ns owlet.components.header
  (:require
    [owlet.utils :refer [hydrate! CONTENTFUL-CREATE]]
    [owlet.components.login :refer [login-component]]
    [reagent.session :as session]
    [reagent.core :as reagent]
    [re-frame.core :as re-frame]))

(defn header-component []
      (let [img-src-subs (re-frame/subscribe [:user-has-background-image?])]
           (fn []
               [:div#header
                [:div.login
                 [login-component]]
                [:button#change-header-btn.btn-primary-outline.btn-sm
                 {:on-click
                  (fn []
                      (let [url (js/prompt "i need a url")]
                           (when url
                                 (CONTENTFUL-CREATE
                                   "/api/content/create/entry"
                                   {:params        {:content-type  "userBgImage"
                                                    :url           url
                                                    :social-id     (session/get :user-id)
                                                    :auto-publish? true}
                                    :handler       (fn [res]
                                                       (println res))
                                    :error-handler (fn [err]
                                                       (println err))}))))} "change me!"]
                [:img {:src (or @img-src-subs "http://eskipaper.com/images/space-4.jpg")}]])))
