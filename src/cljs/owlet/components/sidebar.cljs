(ns owlet.components.sidebar
  (:require
    [reagent.core :refer [atom]]))

(defn sidebar-component []
  [:div.left.col-lg-2.text-center
   [:a {:href "/#/"}
    [:img {:src "img/owlet-logo.png"}]]
   [:div.options
    [:h1 "owlet"]
    [:img {:src "img/icon1.png"}] [:br]
    [:img {:src "img/icon2.png"}] [:br]
    [:a {:href "/#/settings"}
     [:img {:src "img/icon3.png"}]]]])
