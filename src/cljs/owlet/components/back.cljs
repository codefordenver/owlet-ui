(ns owlet.components.back
  (:require [re-frame.core :as rf]))

(defn back []
  [:a {:href "#/branches"}
    [:img.back {:src "/img/back-filled.png"}]])
