(ns owlet-ui.components.track
  (:require [re-frame.core :as re]))

(defn track [data]
  (fn []
    (let [name (.replace (:name data) " Activity" "")]
      [:div.trackwrapper.col-xs-12.col-sm-6.col-lg-4
       [:div.trackwrap
        [:a.track {:on-click #(re/dispatch [:set-activities-by-track-in-view :display-name name])
                   :href (str "#/track/" (:model-id data))}
         [:h2 name]
         [:p (:description data)]]]])))
