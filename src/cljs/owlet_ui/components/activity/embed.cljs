(ns owlet-ui.components.activity.embed)

(defn generic-responsive-iframe
  "returns an responsive iframe"
  [iframe-code]
  (.replace iframe-code (js/RegExp. "/\\\"/g,'\\''")))

(defn activity-embed [activity-data]
  (let [embed (get-in @activity-data [:fields :embed])
        preview (get-in @activity-data [:fields :preview :sys :url])
        skills (get-in @activity-data [:fields :skills])]
    [:div.activity-embed-wrap
     (if (nil? embed)
       [:div.activity-preview
        [:img {:src preview :width "100%"}]]
       [:div.embed-container
        {"dangerouslySetInnerHTML"
         #js{:__html (generic-responsive-iframe embed)}}])
     [:div.activity-concept-wrap
      (for [c skills]
        ^{:key (gensym "concept-")}
        [:span.tag.tag-default c])]]))
