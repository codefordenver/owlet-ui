(ns owlet-ui.components.activity.embed)

(defn- generic-responsive-iframe
  "returns an responsive iframe"
  [iframe-code]
  (.replace iframe-code (js/RegExp. "/\\\"/g,'\\''")))

(defn activity-embed [embed skills preview]
  (let [preview-url (-> preview :sys :url)]
    [:div.activity-embed-wrap.box-shadow
     (if (nil? embed)
       [:div.activity-preview
        [:img {:src preview-url :width "100%"}]]
       [:div.embed-container
        {"dangerouslySetInnerHTML"
         #js{:__html (generic-responsive-iframe embed)}}])
     [:div.activity-concept-wrap
      (for [c skills]
        ^{:key (gensym "concept-")}
        [:span.tag.tag-default c])]]))
