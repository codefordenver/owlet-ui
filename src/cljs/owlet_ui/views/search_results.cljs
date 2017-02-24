(ns owlet-ui.views.search-results
  (:require [re-frame.core :as rf]
            [owlet-ui.components.activity-thumbnail :refer [activity-thumbnail]]
            [owlet-ui.components.back :refer [back]]))

(defn search-results-view []
  (let [search-results @(rf/subscribe [:activities-by-branch-in-view])]
    [:div.branch-activities-wrap
      (if-not search-results
        [:div
          [:h2 [:mark.white.box.box-shadow [:b "Loading..."]]]]
        (if (= search-results "none")
          [:div
            [:h2 [back] [:mark.white.box.box-shadow [:b "Nothing yet, but we're working on it."]]]]
          (let [{:keys [display-name activities]} search-results]
            [:div
              [:h2 [back]
               [:mark.white.box-shadow [:b display-name]]]
              [:div {:style {:text-align "right"}}
               [:mark.white {:style {:background-color "rgba(255,255,255,0.65)"
                                     :margin-right "15px"
                                     :padding ".15em .35em .15em .4em"
                                     :font-weight "500"
                                     :font-size "1.02em"}}
                "* = software required"]]
              [:div.flexcontainer-wrap
               (if (seq activities)
                 (for [activity activities
                       :let [fields (:fields activity)
                             id (get-in fields [:preview :sys :id] (gensym "key-"))
                             entry-id (get-in activity [:sys :id])]]
                   ^{:key id} [activity-thumbnail fields entry-id])
                 [:p.no-activities [:mark "No activities in this branch yet. Check back soon."]])]])))]))
