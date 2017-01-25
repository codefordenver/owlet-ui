(ns owlet-ui.components.lpsidebar
  (:require [owlet-ui.components.login :refer [login-component]]))

(defn lpsidebar-component []
  [:div#lpsidebar
   [:div
    [:a {:href "#"}
     [:img#owlet-image {:src "img/owlet-logo.png"}]]]
   [:div.menu
    [:div.login
     [login-component]]
    [:a {:href "#/branches"}
     [:img {:src "img/icon1.png"}]]]])
    ; [:br]
    ; [:a {:href "#/"}
    ;  [:img {:src "img/icon2.png"}]]
    ; [:br]
    ; [:a {:href "#/settings"}
    ;  [:img {:src "img/icon3.png"}]]]])
