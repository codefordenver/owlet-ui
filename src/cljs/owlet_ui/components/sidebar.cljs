(ns owlet-ui.components.sidebar
  (:require
    [cljsjs.jquery]
    [owlet-ui.components.login :refer [login-component]]
    [reagent.core :refer [atom]]))

(defn sidebar-component []
  [:div
    [:div#lpsidebar
      [:a {:href "#"}
        [:img.owlet {:src "img/owlet-logo-newer.png"}]]
      [:div.menu
        [:div.login
         [login-component]]
        [:a {:href "#/tracks"}
          [:img {:src "img/icon1.png"}]]
        [:br]
        [:a {:href "#/"}
          [:img {:src "img/icon2.png"}]]
        [:br]
        [:a {:href "#/settings"}
          [:img {:src "img/icon3.png"}]]]]])
