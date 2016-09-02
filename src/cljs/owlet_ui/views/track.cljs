(ns owlet-ui.views.track
  (:require [re-frame.core :as re]))


(defn track-view []
    (fn []
      [:div.tracks
        [:div.trackwrapper
         [:a.track {:href "#"}
          [:h1 "Graphic Design"]
          [:p "Sample description"]]]]))
