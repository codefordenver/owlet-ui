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
            [:h1#title "Tracks:"]
            [:br]
            (for [model (:models @activity-models)]
              ^{:key (gensym "model-")}
              [track model])]
           [:div.funfact "༼ つ ◕_◕ ༽つ" [:b " fun fact: "] [:i "Seeing gray dots?"]
            [:br] "Those gray dots don't really exist.. when you try to look at one directly, it disappears. This is a "
              [:a {:href "http://www.newworldencyclopedia.org/entry/Grid_illusion"} "grid illusion"] "."]])})))
