(ns owlet-ui.components.activity.image-gallery
  (:require [cljsjs.photoswipe]
            [cljsjs.photoswipe-ui-default]
            [reagent.core :as reagent :refer [atom]]))

(defn prepare-image-items [image-items]
  ; TODO: pass in image width and height
  (mapv #(hash-map :src (:url %) :w (:w %) :h (:h %)) image-items))

(defn full-screen-gallery []
  [:div.activity-image-gallery-wrap
   [:div.pswp {:tabIndex "-1" :role "dialog" :aria-hidden "true"}
    [:div.pswp__bg]
    [:div.pswp__scroll-wrap
     [:div.pswp__container
      [:div.pswp__item]
      [:div.pswp__item]
      [:div.pswp__item]]
     [:div.pswp__ui.pswp__ui--hidden
      [:div.pswp__top-bar
       [:div.pswp__counter]
       [:button.pswp__button.pswp__button--close {:title "Close (Esc)"}]
       [:button.pswp__button.pswp__button--share {:title "Share"}]
       [:button.pswp__button.pswp__button--fs {:title "Toggle fullscreen"}]
       [:button.pswp__button.pswp__button--zoom {:title "Zoom in/out"}]
       [:div.pswp__preloader
        [:div.pswp__preloader__icn
         [:div.pswp__preloader__cut
          [:div.pswp__preloader__donut]]]]]
      [:div.pswp__share-modal.pswp__share-modal--hidden.pswp__single-tap
       [:div.pswp__share-tooltip]]
      [:button.pswp__button.pswp__button--arrow--left {:title "Previous (arrow left)"}]
      [:button.pswp__button.pswp__button--arrow--right {:title "Next (arrow right)"}]
      [:div.pswp__caption
       [:div.pswp__caption__center]]]]]])


(defn activity-image-gallery [image-items]
  (reagent/create-class
    {:component-did-mount
     #(js/initPhotoSwipeFromDOM ".img-gallery")
     :reagent-render
     (fn []
       (let [images (prepare-image-items image-items)]
         [:div.activity-gallery-wrap
           [:h4 [:mark "See what other people made..."]]
           [:div {:class "img-gallery"
                  :item-scope "true"
                  :item-type "http://schema.org/ImageGallery"}
            (for [image images
                  :let [src (:src image)
                        w (:w image)
                        h (:h image)]]
              ^{:key (gensym "img-")}
              [:figure {:item-prop "associatedMedia"
                        :item-scope "true"
                        :item-type "http://schema.org/ImageObject"}
                [:a {:href src
                     :item-prop "contentUrl"
                     :data-size (str w "x" h)}
                  [:img.box-shadow {:src src
                                    :item-prop "thumbnail"}]]])]
           (full-screen-gallery)]))}))
