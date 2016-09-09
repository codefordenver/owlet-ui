(ns owlet-ui.views.tracks
  (:require [re-frame.core :as re]
            [reagent.core :as reagent :refer [atom]]
            [owlet-ui.components.track :refer [track]]))

(defn tracks-view []
  (let [activities (re/subscribe [:library-activity-models])]
    (reagent/create-class
      {:component-will-mount
       #(when (empty? @activities)
         (re/dispatch [:get-activity-models]))
       :reagent-render
       (fn []
         [:div.tracks
          [:h1#title "Tracks:"]
          (for [model (:models @activities)]
            ^{:key (gensym "model-")}
            [track model])])})))
