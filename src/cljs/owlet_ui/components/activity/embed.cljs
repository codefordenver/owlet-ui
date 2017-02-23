(ns owlet-ui.components.activity.embed
  (:require [re-frame.core :as rf]))

(defn- generic-responsive-iframe
  "returns an responsive iframe"
  [iframe-code]
  (.replace iframe-code (js/RegExp. "/\\\"/g,'\\''")))

(defn activity-embed [embed skills preview]
  (let [preview-url (-> preview :sys :url)]
    [:div.activity-embed-wrap.box-shadow
     (if-not embed
       [:div.activity-preview
        [:img {:src preview-url :width "100%"}]]
       [:div.embed-container
        {"dangerouslySetInnerHTML"
         #js{:__html (generic-responsive-iframe embed)}}])
     (when skills
       [:div.activity-skills-wrap
        [:div.skills
          "SKILLS: "]
        (for [c skills]
         ^{:key (gensym "skill-")}
          [:div {:on-click #(set! (.-location js/window) (str "/#/search/" c))}
            [:span.tag c]])])]))
