(ns owlet-ui.components.activity-thumbnail
  (:require [re-frame.core :as rf]))

(defn- set-as-marked
  "returns component as markdown"
  [field & [class]]
  [:div {:class class
         "dangerouslySetInnerHTML"
                #js{:__html (js/marked (str field))}}])

(defn activity-thumbnail [fields entry-id]
  (let [preview-image-url (get-in fields [:preview :sys :url])
        image (or preview-image-url "img/default-thumbnail.png")
        {:keys [title summary unplugged techRequirements skills]} fields]
    [:div.col-xs-12.col-md-6.col-lg-4
     [:div.activity-thumbnail-wrap.box-shadow
      [:a {:href     (str "#/activity/" entry-id)
           :on-click #(rf/dispatch [:set-activity-in-view entry-id])}
       [:div.activity-thumbnail {:style {:background-image (str "url('" image "')")}}
        [:mark.title title]]]
      (if techRequirements
       [:div.platform-wrap
        [:span "Platform: "]
        [:div.platform.btn
         [set-as-marked techRequirements]]]
       [:div.platform-wrap
        [:div.unplugged.btn
         "UNPLUGGED"]])
      [:div.summary summary]
      (when skills
        (for [c skills]
          ^{:key (gensym "skill-")}
          [:span.tag c]))]]))
