(ns owlet-ui.components.activity.embed
  (:require [re-frame.core :as re]))

(defn activity-embed []
  (let [activity-data (re/subscribe [:activity-in-view])]
    (fn []
      (let [embed (get-in @activity-data [:fields :embed])
            preview (get-in @activity-data [:fields :preview :sys :url])]
        [:div.activity-embed-wrap
          (if (nil? embed)
            [:div.activity-preview
              [:img {:src preview}]]
            [:div.activity-embed {"dangerouslySetInnerHTML"
                                    #js{:__html embed}}])]))))
