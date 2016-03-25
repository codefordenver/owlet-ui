(ns owlet.app
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)
; (add-watch img-src :logger #(-> %4 clj->js js/console.log))

(defn custom-header []
  (let [img-src (reagent/atom "http://eskipaper.com/images/space-1.jpg")]
    (fn []
    [:div.custom-header.no-gutter
      [:button.btn-primary
      {:onClick
       (fn []
         (let [url (js/prompt "i need a url")]
             (when url
                   (reset! img-src url))
             ))} "change me!"]
      [:img {:src @img-src}]
      ])))

(defn main []
  [:div.no-gutter
    [:div.left.col-lg-2.text-center
      [:img {:src "img/owlet-logo.png"}]
      [:div.options
        [:h1 "owlet"]
        [:img {:src "img/icon1.png"}][:br]
        [:img {:src "img/icon2.png"}][:br]
        [:img {:src "img/icon3.png"}]
      ]
    ]
    [:div.right.col-lg-10
      [custom-header]
      [:div.search
        [:input {
          :type "search"
          :name "sitesearch"}]
        [:input {
          :type "submit"
          :value "Search"}]
      ]
    ]
      [:div.content]])


(defn init []
  (reagent/render-component [main]
                            (.getElementById js/document "mount")))
