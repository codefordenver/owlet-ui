(ns owlet.components.header
  (:require
    [owlet.components.login :refer [login-component]]
    [reagent.core :refer [atom]]))

(defonce server-url "http://localhost:3000")

(defn header-component []
      (let [img-src (atom (or (.getItem js/localStorage "custom-image-url")
                              "http://eskipaper.com/images/space-1.jpg"))]
           (fn []
               [:div#header
                [:div.login
                 [login-component]]
                [:button#change-header-btn.btn-primary-outline.btn-sm
                 {:onClick
                  (fn []
                      (let [url (js/prompt "i need a url")]
                           (when url
                                 (do
                                   (.setItem js/localStorage "custom-image-url" url)
                                   (reset! img-src url)))))} "change me!"]
                [:img {:src @img-src}]])))
