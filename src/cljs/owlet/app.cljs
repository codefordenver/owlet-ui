(ns owlet.app
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)
; (add-watch img-src :logger #(-> %4 clj->js js/console.log))

(defn custom-header []
  (let [img-src (reagent/atom "http://eskipaper.com/images/space-1.jpg")]
    (fn []
    [:div.custom-header
      [:img {:src @img-src}]
      [:button.btn-primary
       {:onClick
        (fn []
          (let [url (js/prompt "i need a url")]
              (when url
                    (reset! img-src url))
              ))} "change me!"]])))

(defn main []
  [:div.container-fluid
    [:div.left.col-lg-3
      [:h2 "owlet"]
      [:p "sidebar"]]
    [:div.right.col-lg-9
      [custom-header]
      [:div.content]]])


(defn init []
  (reagent/render-component [main]
                            (.getElementById js/document "mount")))
