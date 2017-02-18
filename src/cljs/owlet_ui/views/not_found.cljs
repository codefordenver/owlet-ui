(ns owlet-ui.views.not-found
  (:require [re-frame.core :as rf]
            [owlet-ui.components.back :refer [back]]))

(defn not-found-view []
  [:div.not-found
   [:h2 [back]
        [:mark.white.box-shadow "Error 404 - Not Found"]]
   [:h3 [:mark.white "Hey there, this activity has moved to another URL."]]
   [:h3 [:mark.white "Chances are you'll find it inside a branch on the"]]
   [:h3 [:mark.white [:a {:href "/branches"} "Branches page :)"]]]])
