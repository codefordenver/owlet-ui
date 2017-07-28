(ns owlet.views.not-found
  (:require [re-frame.core :as rf]
            [owlet.components.back :refer [back]]))

(defn not-found-view []
  [:div.not-found
   [:h2 [:mark.white.box-shadow [back] "Error 404 - Not Found"]]
   [:h3 [:mark.white "Hey there, this activity has moved to another URL."]]
   [:h3 [:mark.white "Chances are you'll find it inside a branch on the"]]
   [:h3 [:mark.white [:a {:href "#/branches"} "Branches page :)"]]]])
