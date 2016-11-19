(ns owlet-ui.components.track
  (:require [re-frame.core :as re]
            [reagent.core :as reagent]
            [clojure.string :as str]))

(defn track [[color data]]
  (fn []
    (reagent/create-class
      {:component-did-mount
       (fn []
         (js/zadenMagic))
       :reagent-render
         (fn []
           (let [name (str/upper-case (:name data))
                 name-line1 (first (str/split name " "))
                 name-line2 (rest (str/split name " "))]
             [:div.trackwrapper.col-xs-12.col-md-6.col-lg-4
              [:div.trackwrap
               [:div.track-bg.box-shadow {:style {:background-color color}}
                 [:a.track {:on-click #(re/dispatch [:set-activities-by-track-in-view :display-name name])
                            :href     (str "#/tracks/" (:model-id data))}
                  [:h2 [:mark name-line1]
                    (when (<= 1 (count name-line2))
                      [:span
                        [:br]
                        [:mark (str/join " " name-line2)]])]]]]]))})))
                  ; [:p (:description data)]]]]))})))
