(ns owlet-ui.views.create-content)

(defn create-content-view []
  (fn []
    [:div.create-content-wrap
      [:div.activity-title-wrap
        [:h1 [:mark.white.box-shadow "Contribute to OWLET!"]]]
      [:div.create-content-inner
        [:p "Here's a breakdown of the content, and how you can contribute."]
        [:br]
        [:h3 "I. Activity Components"]
        [:img {:src "img/create-content/activity-map.jpg"
               :width "100%"
               :style {:padding "0 1em 1.5em 1em"}}]
        [:ol {:type "A"}
         [:b [:li "ACTIVITY NAME and CREATOR (your name or handle)"]]
         [:b [:li "EMBED MEDIA — the meat of your activity, can be any of the following:"]]
         [:ul
          [:li "Slides.com slideshow"]
          [:li "Vimeo video"]
          [:li "Pretty much any <iframe>"]
          [:li "Image or GIF"]
          [:li "If nothing is provided*, it'll automatically display the preview (thumbnail) image"]]
         [:b [:li "INFO — you don't need to fill everything, blank fields won't be displayed"]]
         [:ul
          [:li "Technology needed (you can also choose 'UNPLUGGED' for offline activities)"]
          [:li "Summary"]
          [:li "Why?"]
          [:li "Pre-requisites"]
          [:li "Materials (*if you want to provide a printable PDF instead of embedding media, you can upload it to this section)"]]
         [:b [:li "INSPIRATION — place to give credit and/or provide examples"]]
         [:b [:li "SKILLS — clickable tags"]]
         [:b [:li "CHALLENGE — for students who finish early"]]]]]))
