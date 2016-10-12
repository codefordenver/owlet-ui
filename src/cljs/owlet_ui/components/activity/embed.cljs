(ns owlet-ui.components.activity.embed
  (:require [re-frame.core :as re]))

(defn activity-embed []
  (let [activity-data (re/subscribe [:activity-in-view])]
    (fn []
      (let [embed (get-in @activity-data [:fields :embed])
            preview (get-in @activity-data [:fields :preview :sys :url])
            concepts (get-in @activity-data [:fields :concepts])]
        [:div.activity-embed-wrap
          (if (nil? embed)
            [:div.activity-preview
              [:img {:src preview}]]
            [:div.activity-embed {"dangerouslySetInnerHTML"
                                    #js{:__html embed}}])
         [:div.activity-concept-wrap
          (for [c concepts]
               ^{:key (gensym "concept-")}
               [:span.tag.tag-default c])]]))))

