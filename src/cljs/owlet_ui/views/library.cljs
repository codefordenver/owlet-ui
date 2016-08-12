(ns owlet-ui.views.library
  (:require [re-frame.core :as re-frame]
            [owlet-ui.components.activity-thumbnail :refer [activity-thumbnail-component]]))

(defn library-view []
  (re-frame/dispatch [:get-library-content])
  (let [activities (re-frame/subscribe [:library-activities])]
    (fn []
      [:div.jumbotron
       [:div.container-fluid
        [:div.row
         [:div.col-lg-12
          [:h1 "Library"]
          [:p.text-center
           "library content"]
          (for [a @activities
                :let [image (:activity-picture-url a)
                      title (:title a)]]
            ^{:key (get-in a [:preview :sys :id])}
            [activity-thumbnail-component title image])]]]])))