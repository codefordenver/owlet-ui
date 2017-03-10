(ns owlet-ui.components.activity.comments
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]))

(defn load-comments [activity]
 (let [id (get-in activity [:sys :id])
       url (-> js/document .-location .-href)
       title (get-in activity [:fields :title])]
   (js/disqusReset id url title)))

(defn activity-comments []
  (let [activity @(rf/subscribe [:activity-in-view])]
    (reagent/create-class
       {:component-did-mount #(load-comments activity)
        :reagent-render (fn [] [:div.activity-comments-wrap.box-shadow [:div#disqus_thread]])})))
