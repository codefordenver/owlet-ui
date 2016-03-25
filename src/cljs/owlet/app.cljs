(ns owlet.app
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(defn custom-header []
  (let [img-src (reagent/atom "")]
    ; (add-watch img-src :logger #(-> %4 clj->js js/console.log))
    (fn []
    [:div.custom-header
      {:style
        {:background (str "url('" (str @img-src) "')")
         :backgroundSize "cover"}}
      [:button.btn-primary
       {:onClick
        (fn []
          (let [url (js/prompt "i need a url")]
              (reset! img-src url)
              (print @img-src)
              ))} "change me!"]])))

(defn main []
  [:div.container-fluid
    [:div.left.col-lg-3
      [:h2 "owlet"]
      [:p "sidebar"]]
    [:div.right.col-lg-9
      [custom-header]
      [:div.content]
      [:p "test"]]])


(defn init []
  (reagent/render-component [main]
                            (.getElementById js/document "mount")))
