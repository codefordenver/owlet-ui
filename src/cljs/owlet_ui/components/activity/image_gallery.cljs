(ns owlet-ui.components.activity.image-gallery
  (:require [cljsjs.photoswipe]
            [reagent.core :as reagent :refer [atom]]))

(defonce state (atom nil))

(defn photoswipe-template [state]
  (when (not (nil? @state))
    (.init
      (js/PhotoSwipe. (@state :element)
                      nil
                      (@state :items))))
  [:div.activity-image-gallery-wrap.box-shadow
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
        [:button.pswp__button.pswp__button--zoom  {:title "Zoom in/out"}]
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

(defn prepare-image-items [image-urls]
  (map (fn [url] {:src url :w 400 :h 400}) image-urls))

(defn activity-image-gallery [activity-data]
  (if-let [image-urls (get-in @activity-data [:fields :image-gallery-urls])]
    (reagent/create-class
      {:component-did-mount
       (fn []
        (let [pswp-element (js/document.querySelector ".pswp")]
         (reset! state {:element pswp-element
                        :items (apply array (prepare-image-items image-urls))})))
       :reagent-render
       #(photoswipe-template state)})))
