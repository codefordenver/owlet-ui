(ns owlet-ui.components.activity-thumbnail
  (:require [re-frame.core :as rf]))

(defn set-as-marked
  "returns component as markdown"
  [field & [class]]
  (when field
    [:div {:class class
           "dangerouslySetInnerHTML"
                  #js{:__html (js/marked (str field))}}]))

(defn activity-thumbnail [fields entry-id]
  (let [preview-image-url (get-in fields [:preview :sys :url])
        image (or preview-image-url "img/default-thumbnail.png")
        {:keys [title summary techRequirements]} fields]

    (fn []
      [:div.col-lg-4.col-sm-6.col-xs-12
       [:div.activity-thumbnail-wrap.box-shadow
        [:a {:href (str "#/activity/" entry-id)
             :on-click #(rf/dispatch [:set-activity-in-view entry-id])}
         [:div.activity-thumbnail {:style {:background-image (str "url('" image "')")
                                           :background-size  "cover"}}
          [:mark.title title]]]
        [:div.technology
          [:p "Tech:"]
          [set-as-marked techRequirements]]
        [:div.summary summary]]])))
