(ns owlet-ui.components.lpsidebar
  (:require
    [cljsjs.jquery]
    [owlet-ui.components.login :refer [login-component]]
    [reagent.core :refer [atom]]))

(defn lpsidebar-component []
  [:div
    [:div#lpsidebar
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
