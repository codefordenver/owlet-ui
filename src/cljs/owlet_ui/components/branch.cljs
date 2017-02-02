(ns owlet-ui.components.branch
  (:require [re-frame.core :as re]
            [clojure.string :as str]
            [reagent.core :as reagent :refer [atom]]
            [camel-snake-kebab.core :refer [->kebab-case]]))

(defn branch [[color branch-name] branch-key]
 (let [activities-by-branch (re/subscribe [:activities-by-branch])]
  (let [name (str/upper-case branch-name)
        name-line1 (first (str/split name " "))
        name-line2 (rest (str/split name " "))
        counter (-> @activities-by-branch
                    branch-key
                    :count)
        hover-image (-> @activities-by-branch
                        branch-key
                        :preview-url)]
    [:div.branchwrapper.col-xs-12.col-md-6.col-lg-4
     [:div.branchwrap {:style {:background-image (str "url('" hover-image "')")}}
      [:div.branch-bg.box-shadow {:style {:background-color color
                                          :background-image (str "linear-gradient(to right, " color " 25%, rgba(0,0,0,0) 75%")}}
       [:a.branch {:on-click #(re/dispatch-sync [:set-activities-by-branch-in-view (->kebab-case branch-name)])
                   :href     (str "#/" (->kebab-case branch-name))}
        [:h2 [:mark name-line1]
         (when (<= 1 (count name-line2))
           [:span
            [:br]
            [:mark (str/join " " name-line2)]])]
        [:div.counter
         [:p counter]]]]]])))
