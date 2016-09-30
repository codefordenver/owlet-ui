(ns owlet-ui.components.sidebar
  (:require
    [cljsjs.jquery]
    [reagent.core :refer [atom]]))

(defn sidebar-component []
  [:div
    [:div#lpsidebar
      [:a#logo {:href "#/"}
        [:img.owlet {:src "img/owlet-logo.png"}]]
      [:div.menu
        [:a {:href "#/tracks"}
          [:img {:src "img/icon1.png"}]]
        [:a {:href "#/"}
          [:img {:src "img/icon2.png"}]]
        [:a {:href "#/settings"}
          [:img {:src "img/icon3.png"}]]]]

    [:div#sidebar
      [:a {:href "#/"}
        [:img.owlet {:src "img/owlet-logo.png"}]]
      [:div.menu
        [:span
          [:h1 "owlet"]
          [:ul.nav.nav-pills.nav-stacked
            [:li.nav-item
              [:a.nav-link {:href "#/tracks"}
                [:img {:src "img/icon1.png"}]]]
            [:li.nav-item
              [:a.nav-link {:href "#/"}
                [:img {:src "img/icon2.png"}]]]
            [:li.nav-item
              [:a.nav-link {:href "#/settings"}
                [:img {:src "img/icon3.png"}]]]]]]]])
