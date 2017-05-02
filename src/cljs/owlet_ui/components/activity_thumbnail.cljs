(ns owlet-ui.components.activity-thumbnail
  (:require [re-com.core :as re-com :refer-macros [handler-fn]]
            [re-com.popover]
            [cljsjs.bootstrap]
            [re-frame.core :as rf]
            [reagent.core :as reagent]))

(defn activity-thumbnail [fields entry-id]
  (let [preview-image-url (get-in fields [:preview :sys :url])
        image (or preview-image-url "img/default-thumbnail.png")
        {:keys [title summary unplugged platform skills]} fields
        platform-name (:name platform)
        platform-color (:color platform)
        showing? (reagent/atom false)]
    [:div.col-xs-12.col-md-6.col-lg-4
     [:div.activity-thumbnail-wrap.box-shadow
      [:a {:href     (str "#/activity/#!" entry-id)
           :on-click #(rf/dispatch [:set-activity-in-view entry-id])}
       [:div.activity-thumbnail {:style {:background-image (str "url('" image "')")}}
        [:mark.title title]]]
      (if platform
       [:div.platform-wrap
        [:b "Platform: "]
        [re-com/popover-anchor-wrapper
         :showing? showing?
         :position :below-left
         :anchor [:div.platform.btn
                  {:on-click #(rf/dispatch [:filter-activities-by-search-term platform-name])
                   :style {:background-color platform-color}
                   :on-mouse-over (handler-fn (reset! showing? true))
                   :on-mouse-out  (handler-fn (reset! showing? false))}
                  platform-name]
         :popover [re-com/popover-content-wrapper
                   :close-button? false
                   :body "Click for more info"]]]
       [:div.platform-wrap
         [re-com/popover-anchor-wrapper
          :showing? showing?
          :position :below-left
          :anchor [:div.unplugged.btn
                   {:on-mouse-over (handler-fn (reset! showing? true))
                    :on-mouse-out  (handler-fn (reset! showing? false))}
                   "UNPLUGGED"]
          :popover [re-com/popover-content-wrapper
                    :close-button? false
                    :body "Does not require a computer or device"]]])
      [:div.summary summary]
      (when skills
        (for [skill skills]
          ^{:key (gensym "skill-")}
          [:div.tag {:on-click #(rf/dispatch [:filter-activities-by-search-term skill])}
            [:span skill]]))]]))
