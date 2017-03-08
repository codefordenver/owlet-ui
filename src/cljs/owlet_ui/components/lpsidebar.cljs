(ns owlet-ui.components.lpsidebar
  (:require [owlet-ui.components.login :refer [login-component]]
            [re-frame.core :as re]))

(defn lpsidebar-component []
  [:div#lpsidebar
   [:div#owlet-logo-div
    [:a#owlet-image {:href "#"}
      [:img#owlet-owl {:src "../img/owlet-owl.png"}]]]
   [:div.menu
    [:div.login
     [login-component]]
    [:a.navigation {:href "#/branches"
                    :on-click #(re/dispatch [:set-active-view :branches-view])}
     [:img {:src "img/icon1.png"}]]]])
    ; [:br]
    ; [:a {:href "#"}
    ;  [:img {:src "img/icon2.png"}]]
    ; [:br]
    ; [:a {:href "#/settings"}
    ;  [:img {:src "img/icon3.png"}]]]])
