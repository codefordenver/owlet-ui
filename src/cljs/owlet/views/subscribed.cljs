(ns owlet.views.subscribed
  (:require [owlet.components.back :refer [back]]
            [re-frame.core :as rf]))

(defn subscribed-view []
  [:div.not-found
   [:h2 [:mark.white.box-shadow [back] "Thanks! You are now subscribed."]]
   [:h3 [:mark.white "We will send an email notification to "
                     [:span {:style {:color "#0275d8"}}
                        @(rf/subscribe [:subscriber-email])]
                     " whenever a new activity is posted."]]])
