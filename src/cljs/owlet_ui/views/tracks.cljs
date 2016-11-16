(ns owlet-ui.views.tracks
  (:require [re-frame.core :as re]
            [reagent.core :as reagent :refer [atom]]
            [owlet-ui.components.track :refer [track]]))

(defn pair-color [activity-models]
  (let [colors ["#FF563E" "#00cbb2" "#3d8142" "#41bba2" "#e1bb00" "#254e68" "#dd0067" "#d37fe6" "#e00000"]]
    (map vector colors activity-models)))

(defn tracks-view []
  (let [activity-models (re/subscribe [:library-activity-models])]
    (reagent/create-class
      {:component-will-mount
       #(when (empty? @activity-models)
         (re/dispatch [:get-activity-models]))
       :reagent-render
       (fn []
         [:div.height-wrap
           [:div.tracks
            [:h1#title [:mark "Get started by choosing a track below"]]
            [:br]
            (let [color-pairs (pair-color (:models @activity-models))]
              (for [pair color-pairs]
                ^{:key (gensym "model-")}
                [track pair]))]])})))
