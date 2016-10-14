(ns owlet-ui.views.tracks
  (:require [re-frame.core :as re]
            [reagent.core :as reagent :refer [atom]]
            [owlet-ui.components.track :refer [track]]))

(defn tracks-view []
  (let [activity-models (re/subscribe [:library-activity-models])]
    (reagent/create-class
      {:component-will-mount
       #(when (empty? @activity-models)
         (re/dispatch [:get-activity-models]))
       :reagent-render
       (fn []
         [:div
           [:div.tracks
            [:h1#title "Get started by choosing a track below"]
            [:br]
            (for [model (:models @activity-models)]
              ^{:key (gensym "model-")}
              [track model])]])})))
