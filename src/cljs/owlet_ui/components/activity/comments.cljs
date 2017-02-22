(ns owlet-ui.components.activity.comments
  (:require [reagent.core :as reagent]
            [re-frame.core :as re]))

(defn load-comments [activity]
  (js/disqusReset
    (get-in activity [:sys :id])
    (-> js/document .-location .-href)
    (get-in activity [:fields :title])))

(defn activity-comments []
  (let [activity @(re/subscribe [:activity-in-view])]
    (reagent/create-class
       {
        :component-did-mount #(load-comments activity)
        :reagent-render ( fn [] [:div.activity-comments-wrap [:div#disqus_thread]])})))
