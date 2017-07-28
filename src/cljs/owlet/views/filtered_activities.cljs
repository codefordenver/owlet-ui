(ns owlet.views.filtered-activities
  (:require [re-frame.core :as rf]
            [owlet.components.activity-thumbnail :refer [activity-thumbnail]]
            [owlet.components.back :refer [back]]
            [owlet.helpers :refer [showdown]]
            [owlet.components.email-notification :refer [email-notification]]))

(defn filtered-activities-view []
  (let [filtered-activities @(rf/subscribe [:activities-by-filter])]
    [:div.branch-activities-wrap
     [email-notification]
     (if-not filtered-activities
       [:h2.pushed-left [:mark.white [:b "Loading..."]]]
       (if (= filtered-activities "error")
         [:h2.pushed-left [:mark.white.box [back] [:b "Nothing here. Try a different search above."]]]
         (let [{:keys [display-name activities & description]} filtered-activities]
           [:div
            [:h2.pushed-left [:mark.white [back] [:b display-name]]]
            (if description
              ; filtering by platform
              [:div
                 [:div {:class "platform-description"
                        "dangerouslySetInnerHTML"
                               #js{:__html (.makeHtml showdown description)}}]
                 [:div {:style {:margin-left "15px"}}
                  [:h3 {:style {:margin-bottom "15px"
                                :margin-top "40px"}}
                    [:mark.white [:b "Activities"]]]]]
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
              [:p.no-activities [:mark (str "Nothing yet, but we're working on it.")]])]])))]))
