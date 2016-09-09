(ns owlet-ui.components.track)

(defn track [data]
  (fn []
    (let [name (.replace (:name data) " Activity" "")]
      [:div.trackwrapper.col-xs-12.col-sm-6.col-lg-4
       [:div.trackwrap
        [:a.track {:href "#"}
         [:h2 name]
         [:p (:description data)]]]])))
