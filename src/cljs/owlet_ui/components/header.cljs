(ns owlet-ui.components.header
  (:require [reagent.core :as reagent]
            [owlet-ui.components.upload-image-modal :refer [upload-image-component]]
            [re-frame.core :as rf]))

(defonce show? (reagent/atom false))

(defn header-component []
  (let [src (rf/subscribe [:my-background-image-url])
        is-user-logged-in? (rf/subscribe [:my-identity])]
    (fn []
      [:div#header {:style {:position "fixed"
                            :width "100%"
                            :height "100%"
                            :background-image (str "url(" @src ")")
                            :background-size "cover"
                            :z-index "-1"}}
       [upload-image-component]
       [:button#change-header-btn
        {:type     "button"
         :class    "btn btn-secondary"
         :style    {:font-size "1em"
                    :padding   "6px"
                    :z-index   "10 !important"
                    :display   (if @is-user-logged-in?
                                 "block"
                                 "none")}
         :on-click (rf/dispatch [:show-bg-img-upload true])}
        [:i.fa.fa-pencil-square-o]]])))
