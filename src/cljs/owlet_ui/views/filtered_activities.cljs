(ns owlet-ui.views.filtered-activities
  (:require [re-frame.core :as rf]
            [owlet-ui.components.activity-thumbnail :refer [activity-thumbnail]]
            [owlet-ui.components.back :refer [back]]
            [owlet-ui.helpers :refer [showdown]]))

(defn filtered-activities-component [filter]
  (let [filtered-activities @(rf/subscribe [:activities-by-filter])]
    [:div.branch-activities-wrap
     (if-not filtered-activities
       [:h2 [:mark.white.box [:b "Loading..."]]]
       (if (= filtered-activities "error")
         [:h2 [back] [:mark.white.box [:b "Nothing yet, but we're working on it."]]]
         (let [{:keys [display-name activities & description]} filtered-activities]
           [:div
            [:h2 [back]
             [:mark.white [:b display-name]]]
            (if description
              ; filtering by platform
              [:div
                 [:div {:class "platform-description"
                        "dangerouslySetInnerHTML"
                               #js{:__html (.makeHtml showdown description)}}]
                 [:div {:style {:margin-left "15px"}}
                  [:h3 [:mark.white [:b "Activities"]]]]]
              ; filtering by branch or skill
              [:div {:style {:text-align "right"}}
                 [:mark.white {:style {:background-color "rgba(255,255,255,0.65)"
                                       :margin-right "15px"
                                       :padding ".15em .35em .15em .4em"
                                       :font-weight "500"
                                       :font-size "1.02em"}}
                  "* = software required"]])
            [:div.flexcontainer-wrap
             (if (seq activities)
               (for [activity activities
                     :let [fields (:fields activity)
                           entry-id (get-in activity [:sys :id])]]
                 ^{:key [entry-id (gensym "key-")]} [activity-thumbnail fields entry-id])
              [:p.no-activities [:mark (str "No activities for this " filter " yet. Check back soon.")]])]])))]))

(defmulti filtered-activities-view identity)

(defmethod filtered-activities-view :skill []
  [filtered-activities-component "skill"])

(defmethod filtered-activities-view :platform []
  [filtered-activities-component "platform"])

(defmethod filtered-activities-view :branch []
  [filtered-activities-component "branch"])
