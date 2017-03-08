(ns owlet-ui.components.activity.comments
  (:require [reagent.core :as reagent]
            [re-frame.core :as re]
            [clojure.core :refer-macros [this-as]]))

(defn load-comments [activity]
  (let [dsq (js/document.createElement "script")
        body (aget (js/document.getElementsByTagName "body") 0)]
    (set! (-> dsq .-type) "text/javascript")
    (set! (-> dsq .-async) true)
    (set! (-> dsq .-src) "//owlet-2.disqus.com/embed.js")
    (.appendChild body dsq)))




(defn activity-comments []
  (let [activity @(re/subscribe [:activity-in-view])]
    (reagent/create-class
       {:component-did-mount (fn []
                                (load-comments activity))
        :reagent-render ( fn [] [:div.activity-comments-wrap [:div#disqus_thread]])})))
