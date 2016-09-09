(ns owlet-ui.views.tracks
  (:require [re-frame.core :as re]
            [reagent.core :as reagent :refer [atom]]))

(defn tracks-view []
  (let [activities (re/subscribe [:library-activity-models])]
    (reagent/create-class
      {:component-will-mount
       #(when (empty? @activities)
         (re/dispatch [:get-activity-models]))
       :reagent-render
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
             [:p "Sample description"]]]]])})))
