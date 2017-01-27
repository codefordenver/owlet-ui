(ns owlet-ui.components.sidebar
  (:require [owlet-ui.components.login :refer [login-component]]
            [re-frame.core :as re]))

(defn sidebar-component []
  [:div#sidebar
   [:div
    [:a {:href "#"}
     [:img#owlet-image {:src "img/owlet-logo.png"}]]]
   [:div.menu
    [:div.login
     [login-component]]
    [:a.navigation {:on-click #(re/dispatch [:set-active-view :branches-view])}
     [:img {:src "img/icon1.png"}]]]])
    ; [:br]
    ; [:a {:href "#/"}
    ;  [:img {:src "img/icon2.png"}]]
    ; [:br]
    ; [:a {:href "#/settings"}
    ;  [:img {:src "img/icon3.png"}]]]])
