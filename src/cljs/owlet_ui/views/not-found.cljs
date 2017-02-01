(ns owlet-ui.views.not-found)


(defn not-found-view []
  [:div.not-found
   [:h2 [:a {:href "#/branches"} [:img.back {:src "img/back-filled.png"}]]
        [:mark.white.box-shadow "Error 404 - Not Found"]]
   [:h3 [:mark.white.box-shadow "Please return to branches view"]]])
