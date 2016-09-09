(ns owlet-ui.views.tracks
  (:require [re-frame.core :as re]))


(defn tracks-view []
    (re/dispatch [:get-activity-models])
    (let [activities (re/subscribe [:library-activity-models])]
      (fn []
       [:div.tracks
         [:h1#title "Tracks:"]
         [:div.trackwrapper.col-xs-12.col-sm-6.col-lg-4
           [:div.trackwrap
            [:a.track {:href "#"}
             [:h2 "Graphic Design"]
             [:p "Sample description"]]]]
         [:div.trackwrapper.col-xs-12.col-sm-6.col-lg-4
           [:div.trackwrap
             [:a.track {:href "#"}
               [:h2 "Graphic Design"]
               [:p "Sample description"]]]]
         [:div.trackwrapper.col-xs-12.col-sm-6.col-lg-4
           [:div.trackwrap
             [:a.track {:href "#"}
               [:h2 "Graphic Design"]
               [:p "Sample description"]]]]
         [:div.trackwrapper.col-xs-12.col-sm-6.col-lg-4
           [:div.trackwrap
             [:a.track {:href "#"}
               [:h2 "Graphic Design"]
               [:p "Sample description"]]]]
         [:div.trackwrapper.col-xs-12.col-sm-6.col-lg-4
           [:div.trackwrap
             [:a.track {:href "#"}
               [:h2 "Graphic Design"]
               [:p "Sample description"]]]]
         [:div.trackwrapper.col-xs-12.col-sm-6.col-lg-4
           [:div.trackwrap
             [:a.track {:href "#"}
               [:h2 "Graphic Design"]
               [:p "Sample description"]]]]
         [:div.trackwrapper.col-xs-12.col-sm-6.col-lg-4
           [:div.trackwrap
             [:a.track {:href "#"}
               [:h2 "Graphic Design"]
               [:p "Sample description"]]]]
         [:div.trackwrapper.col-xs-12.col-sm-6.col-lg-4
           [:div.trackwrap
             [:a.track {:href "#"}
               [:h2 "Graphic Design"]
               [:p "Sample description"]]]]
            ;;TODO make uneven track automatically centered
         [:div.trackwrapper.col-xs-12.col-sm-6.col-lg-4.col-sm-offset-3.col-lg-offset-0
           [:div.trackwrap
             [:a.track {:href "#"}
               [:h2 "Graphic Design"]
               [:p "Sample description"]]]]])))
