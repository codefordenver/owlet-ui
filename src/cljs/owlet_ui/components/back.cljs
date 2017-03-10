(ns owlet-ui.components.back
  (:require [re-frame.core :as rf]))

(defn back []
  [:a {:href "#/branches"
       :on-click #(rf/dispatch [:set-active-view :branches-view])}
    [:img.back {:src "/img/back-filled.png"}]])
