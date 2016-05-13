(ns owlet.components.header
  (:require
    [owlet.utils :refer [hydrate!]]
    [owlet.components.login :refer [login-component]]
    [ajax.core :refer [POST]]
    [reagent.session :as session]
    [reagent.core :as reagent :refer [atom]]))

(defonce server-url "http://localhost:3000")

(defn header-component [& [url]]
      (let [img-src (atom nil)
            ;; (or url "http://eskipaper.com/images/space-2.jpg")
            _ (println url)
            ]
           (reagent/create-class
             {:reagent-render
              (fn []
                  [:div#header
                   [:div.login
                    [login-component]]
                   [:button#change-header-btn.btn-primary-outline.btn-sm
                    {:on-click
                     (fn []
                         (let [url (js/prompt "i need a url")]
                              (when url
                                    (POST (str server-url "/api/content/create/entry")
                                          {:params        {:content-type  "userBgImage"
                                                           :url           url
                                                           :social-id     (session/get :user-id)
                                                           :auto-publish? true}
                                           :handler       (fn [res]
                                                              (println res)
                                                              (reset! img-src url))
                                           :error-handler (fn [err]
                                                              (println err))}))))} "change me!"]
                   [:img {:src @img-src}]])})))
