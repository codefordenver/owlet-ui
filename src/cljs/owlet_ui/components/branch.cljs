(ns owlet-ui.components.branch
  (:require [re-frame.core :as re]
            [reagent.core :as reagent]
            [clojure.string :as str]))

(defn branch [[color branch]]
  (fn []
    (reagent/create-class
      {:component-did-mount
       (fn []
         (js/zadenMagic))
       :reagent-render
         (fn []
           (let [name (str/upper-case branch)
                 name-line1 (first (str/split name " "))
                 name-line2 (rest (str/split name " "))]
             [:div.branchwrapper.col-xs-12.col-md-6.col-lg-4
              [:div.branchwrap
               [:div.branch-bg.box-shadow {:style {:background-color color}}
                 [:a.branch {:on-click #(re/dispatch [:set-activities-by-track-in-view :display-name name])}
                            ;:href     (str "#/branches/" (:model-id branch))}
                  [:h2 [:mark name-line1]
                    (when (<= 1 (count name-line2))
                      [:span
                        [:br]
                        [:mark (str/join " " name-line2)]])]]]]]))})))
