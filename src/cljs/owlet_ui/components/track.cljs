(ns owlet-ui.components.track)

(defn track [data]
  (fn []
    [:div.trackwrapper.col-xs-12.col-sm-6.col-lg-4
     [:div.trackwrap
      [:a.track {:href "#"}
       [:h2 (:name data)]
       [:p (:description data)]]]]))
