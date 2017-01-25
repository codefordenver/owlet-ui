(ns owlet-ui.components.branch
  (:require [re-frame.core :as re]
            [clojure.string :as str]
            [reagent.core :as reagent :refer [atom]]
            [camel-snake-kebab.core :refer [->kebab-case]]))

(defn branch [[color branch] counter hover-image]
  (let [name (str/upper-case branch)
        name-line1 (first (str/split name " "))
        name-line2 (rest (str/split name " "))
        _(prn hover-image)]
    [:div.branchwrapper.col-xs-12.col-md-6.col-lg-4
     [:div.branchwrap
      [:div.branch-bg.box-shadow {:style {:background-image (str "url('" hover-image "')")
                                          :background-size "cover"
                                          :background-color "rgba(0,0,0,0.4)"
                                          :background-blend-mode "multiply"}}
       [:div.branch-color {:style {:background-color color}}]
       [:a.branch {:on-click #(re/dispatch-sync [:set-activities-by-branch-in-view (->kebab-case branch)])
                   :href     (str "#/" (->kebab-case branch))}
         [:div.branch-link
          [:h2 [:mark name-line1]
           (when (<= 1 (count name-line2))
             [:span
              [:br]
              [:mark (str/join " " name-line2)]])]]
         [:div.counter
          [:p counter]]]]]]))
