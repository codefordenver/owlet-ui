(ns owlet.components.header
  (:require [reagent.core :refer [atom]]))

(defonce server-url "http://localhost:3000")

(defn header-component []
      (let [img-src (atom (or (.getItem js/localStorage "custom-image-url")
                              "http://eskipaper.com/images/space-1.jpg"))]
           (fn []
               [:div.custom-header.no-gutter
                [:button.btn-primary
                 {:onClick
                  (fn []
                      (let [url (js/prompt "i need a url")]
                           (when url
                                 (do
                                   (.setItem js/localStorage "custom-image-url" url)
                                   (reset! img-src url)))))} "change me!"]
                [:img {:src @img-src}]])))