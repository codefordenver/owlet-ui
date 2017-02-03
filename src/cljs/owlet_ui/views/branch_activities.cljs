(ns owlet-ui.views.branch-activities
  (:require [re-frame.core :as rf]
            [owlet-ui.components.search-bar :refer [search-bar]]
            [owlet-ui.components.activity-thumbnail :refer [activity-thumbnail]]))

(defn branch-activities-view []
  (let [{:keys [display-name activities]} @(rf/subscribe [:activities-by-branch-in-view])]
      [:div
        [search-bar]
        [:div.branch-activities-wrap
         (if-not display-name
           [:div
            [:h2 [:mark.white.box.box-shadow [:b "This branch does not exist"]]]]
           [:div
            [:h2 [:a {:href "#/branches"} [:img.back {:src "img/back-filled.png"}]]
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
               [:p.no-activities [:mark "No activities in this branch yet. Check back soon."]])]])]]))
