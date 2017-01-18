(ns owlet-ui.components.branch
  (:require [re-frame.core :as re]
            [clojure.string :as str]
            [camel-snake-kebab.core :refer [->kebab-case]]))

(defn branch [[color branch]]
  (let [name (str/upper-case branch)
        name-line1 (first (str/split name " "))
        name-line2 (rest (str/split name " "))]
    [:div.branchwrapper.col-xs-12.col-md-6.col-lg-4
     [:div.branchwrap
      [:div.branch-bg.box-shadow {:style {:background-color color}}
       [:a.branch {:on-click #(re/dispatch-sync [:set-activities-by-branch-in-view (->kebab-case branch)])
                   :href     (str "#/" (->kebab-case branch))}
        [:h2 [:mark name-line1]
         (when (<= 1 (count name-line2))
           [:span
            [:br]
            [:mark (str/join " " name-line2)]])]]]]]))