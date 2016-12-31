(ns owlet-ui.components.activity-thumbnail
  (:require [re-frame.core :as rf]))

(defn activity-thumbnail [fields entry-id]
  (let [preview-image-url (get-in fields [:preview :sys :url])
        image (or preview-image-url "img/default-thumbnail.png")
        {:keys [title summary]} fields]
    (fn []
      [:div.col-lg-4.col-sm-6.col-xs-12
       [:div.activity-thumbnail-wrap.box-shadow
        [:a {:href (str "#/activity/" entry-id)
             :on-click #(rf/dispatch [:set-activity-in-view entry-id])}
         [:div.activity-thumbnail {:style {:background-image (str "url('" image "')")
                                           :background-size  "cover"}}
          [:mark.title title]]]
        [:div.summary summary]]])))
