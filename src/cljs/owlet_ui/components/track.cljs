(ns owlet-ui.components.track
  (:require [re-frame.core :as re]
            [reagent.core :as reagent]
            [clojure.string :as str]))

(defn track [data]
  (fn []
    (reagent/create-class
      {:component-did-mount
       (fn []
         (js/zadenMagic))
       :reagent-render
       (fn []
         (let [name (str/upper-case (:name data))]
           [:div.trackwrapper.col-xs-12.col-md-6.col-lg-4
            [:div.trackwrap
             [:a.track {:on-click #(re/dispatch [:set-activities-by-track-in-view :display-name name])
                        :href     (str "#/tracks/" (:model-id data))}
              [:h2 [:mark (first (str/split name " "))]
                (when (<= 1 (count (rest (str/split name " "))))
                   [:span
                    [:br]
                    [:mark (str/join " " (rest (str/split name " ")))]])]]]]))})))
              ; [:p (:description data)]]]]))})))
