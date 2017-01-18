(ns owlet-ui.components.sidebar
  (:require [owlet-ui.components.login :refer [login-component]]))

(defn sidebar-component []
  [:div#sidebar
   [:div#owlet-wrap
    [:a {:href "#"}
     [:img#owlet-image {:src "img/owlet-logo.png"}]
     [:p#owlet-text "OWLET"]]]
   [:div.menu
    [:div.login
     [login-component]]
    [:a {:href "#/branches"}
     [:img {:src "img/icon1.png"}]]
    [:br]
    [:a {:href "#/"}
     [:img {:src "img/icon2.png"}]]
    [:br]
    [:a {:href "#/settings"}
     [:img {:src "img/icon3.png"}]]]])
