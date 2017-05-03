(ns owlet-ui.views.filtered-activities
  (:require [re-frame.core :as rf]
            [owlet-ui.components.activity-thumbnail :refer [activity-thumbnail]]
            [owlet-ui.components.back :refer [back]]
            [owlet-ui.helpers :refer [showdown]]))

(defmulti filtered-activities-view identity)

;; TODO: refactor this a more concise component

(defmethod filtered-activities-view :skill []
  (let [search-results @(rf/subscribe [:activities-by-branch-in-view])]
        ;route-params @(rf/subscribe [:route-params])]
    [:div.branch-activities-wrap
     (if-not search-results
       [:h2 [:mark.white.box [:b "Loading..."]]]
       (if (= search-results "none")
         [:h2 [back] [:mark.white.box [:b "Nothing yet, but we're working on it."]]]
         (let [{:keys [display-name activities & description]} search-results]
           [:div
            [:h2 [back]
             [:mark.white [:b display-name]]]
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


(defmethod filtered-activities-view :platform []
  (let [search-results @(rf/subscribe [:activities-by-branch-in-view])]
    [:div.branch-activities-wrap
     (if-not search-results
       [:h2 [:mark.white.box [:b "Loading..."]]]
       (if (= search-results "none")
         [:h2 [back] [:mark.white.box [:b "Nothing yet, but we're working on it."]]]
         (let [{:keys [display-name activities & description]} search-results]
           [:div
            [:h2 [back]
             [:mark.white [:b display-name]]]
            (when-not description
              [:div {:style {:text-align "right"}}
               [:mark.white {:style {:background-color "rgba(255,255,255,0.65)"
                                     :margin-right "15px"
                                     :padding ".15em .35em .15em .4em"
                                     :font-weight "500"
                                     :font-size "1.02em"}}
                "* = software required"]])
            (when description
              [:div
               [:div {:class "platform-description"
                      "dangerouslySetInnerHTML"
                             #js{:__html (.makeHtml showdown description)}}]
               [:div {:style {:margin-left "15px"}}
                [:h3 [:mark.white [:b "Projects"]]]]])
            [:div.flexcontainer-wrap
             (if (seq activities)
               (for [activity activities
                     :let [fields (:fields activity)
                           id (get-in fields [:preview :sys :id] (gensym "key-"))
                           entry-id (get-in activity [:sys :id])]]
                 ^{:key id} [activity-thumbnail fields entry-id])
               [:p.no-activities [:mark "No activities in this branch yet. Check back soon."]])]])))]))


(defmethod filtered-activities-view :branch []
  (let [branch-activities @(rf/subscribe [:activities-by-branch-in-view])]
    [:div.branch-activities-wrap
     (if-not branch-activities
       [:div
        [:h2 [:mark.white.box [:b "Loading..."]]]]
       (if (= branch-activities "error")
         [:div
          [:h2 [back] [:mark.white.box [:b "This branch does not exist"]]]]
         (let [{:keys [display-name activities]} branch-activities]
           [:div
            [:h2 [back]
             [:mark.white [:b display-name]]]
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
                           entry-id (get-in activity [:sys :id])]]
                 ^{:key [entry-id (gensym "key-")]} [activity-thumbnail fields entry-id])
               [:p.no-activities [:mark "No activities in this branch yet. Check back soon."]])]])))]))
