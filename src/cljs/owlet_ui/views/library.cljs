(ns owlet-ui.views.library
  (:require [re-frame.core :as re]
            [owlet-ui.components.activity-thumbnail :refer [activity-thumbnail]]))


(defn library-view []
  (let [activities (re/subscribe [:get-library-content])]
    (fn []
      [:div.jumbotron
       [:div.container-fluid
        [:div.row
         [:div.col-lg-12
          [:h1 "Library"]
          [:p.text-center "library content"]
          (for [activity @activities
                :let [fields (:fields activity)
                      id     (get-in fields [:preview :sys :id])
                      image  (get-in fields [:preview :sys :url])
                      title  (:title fields)]]
            ^{:key id}
            [activity-thumbnail title image])]]]])))
