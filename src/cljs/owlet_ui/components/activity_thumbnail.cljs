(ns owlet-ui.components.activity-thumbnail
    (:require [reagent.core :as reagent]))

(defn activity-thumbnail [fields url]
  (let [image (get-in fields [:preview :sys :url] "img/owlet-logo.png")
        {:keys [title track-id summary]} fields]
    (fn []
      [:div.col-lg-4.col-sm-6.col-xs-12
          [:div.activity-thumbnail-wrap.box-shadow
            [:a {:href (str "#/tracks/" track-id "/" url)}
              [:div.activity-thumbnail {:style {:background-image (str "url('" image "')")
                                                :background-size "cover"}}
                [:mark.title title]]]
            [:div.summary summary]]])))
