(ns owlet-ui.components.track
  (:require [re-frame.core :as re]
            [reagent.core :as reagent]))

(defn track [data]
  (fn []
    (reagent/create-class
      {:component-did-mount
       (fn []
         (js/zadenMagic))
       :reagent-render
       (fn []
         (let [name (:name data)]
           [:div.trackwrapper.col-xs-12.col-md-6.col-lg-4
            [:div.trackwrap
             [:a.track {:on-click #(re/dispatch [:set-activities-by-track-in-view :display-name name])
                        :href     (str "#/tracks/" (:model-id data))}
              [:h2 name]
              [:p (:description data)]]]]))})))
