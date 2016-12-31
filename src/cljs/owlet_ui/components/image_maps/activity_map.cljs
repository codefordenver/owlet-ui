(ns owlet-ui.components.image-maps.activity-map
  (:require [reagent.core :as reagent :refer [atom]]
            [cljsjs.jquery]
            [re-com.core :as re-com :refer-macros [handler-fn]]
            [re-com.popover]))

(defonce showing-a? (reagent/atom false))
(defonce showing-b? (reagent/atom false))
(defonce showing-c? (reagent/atom false))
(defonce showing-d? (reagent/atom false))
(defonce showing-e? (reagent/atom false))
(defonce showing-f? (reagent/atom false))

(defn activity-map []
  (reagent/create-class
    {:component-did-mount
     (fn []
      (.rwdImageMaps (js/$ "img[usemap]")))
     :reagent-render
     (fn []
      [:div
       [:img {:src "img/create-content/activity-map.png"
              :useMap "#image-map"
              :style {:margin-bottom "1.5em"
                      :border "1px solid black"
                      :width "100%"}}]
       [:map {:name "image-map"}
        [:div {:style {:position "absolute"}}
          [re-com/popover-anchor-wrapper
            :showing? showing-a?
            :style {:position "absolute"}
            :anchor   [:area#a {:shape "rect"
                                :coords "3,86,54,141"
                                :on-mouse-over (handler-fn (reset! showing-a? true))
                                :on-mouse-out  (handler-fn (reset! showing-a? false))}]
            :position :above-left
            :popover  [re-com/popover-content-wrapper
                       :style {:position "fixed"
                               :top 100
                               :left 100}
                       :close-button? false
                       :title    "What does this mean?"
                       :body     "UNPLUGGED activities do not require a computer or device"]]]
        [:area#b {:shape "rect"
                  :coords "633,177,682,232"
                  :onMouseOver ""
                  :onMouseOut ""}]
        [:area#c {:shape "rect"
                  :coords "974,175,1023,233"
                  :onMouseOver ""
                  :onMouseOut ""}]
        [:area#d {:shape "rect"
                  :coords "703,682,752,740"
                  :onMouseOver ""
                  :onMouseOut ""}]
        [:area#e {:shape "rect"
                  :coords "278,635,324,691"
                  :onMouseOver ""
                  :onMouseOut ""}]
        [:area#f {:shape "rect"
                  :coords "27,704,69,761"
                  :onMouseOver ""
                  :onMouseOut ""}]]])}))
