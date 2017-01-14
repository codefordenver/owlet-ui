(ns owlet-ui.components.header
  (:require [reagent.core :as reagent]
            [owlet-ui.components.upload-image-modal :refer [upload-image-component]]
            [re-frame.core :as re-frame]))

(defonce show? (reagent/atom false))

(defn header-component []
  (let [src (re-frame/subscribe [:user-has-background-image?])
        is-user-logged-in? (re-frame/subscribe [:is-user-logged-in?])
        open-modal (fn [_] (reset! show? true))
        close-modal (fn [_] (reset! show? false))]
    (fn []
      [:div#header {:style {:position "fixed"
                            :width "100%"
                            :height "100%"
                            :background-image (str "url(" @src ")")
                            :background-size "cover"
                            :z-index "-1"}}
       [upload-image-component show? close-modal]
       [:button#change-header-btn
        {:type     "button"
         :class    "btn btn-secondary"
         :style    {:font-size "1em"
                    :padding "6px"
                    :z-index "10 !important"
                    :display (if @is-user-logged-in?
                               "block"
                               "none")}
         :on-click open-modal}
        [:i.fa.fa-pencil-square-o]]])))
