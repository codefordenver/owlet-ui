(ns owlet.components.sidebar
  (:require
    [reagent.core :refer [atom]]))

(defn sidebar-component []
   [:div#sidebar.col-md-3.col-lg-2.sidebar-offcanvas
     [:a {:href "/#/"}
      [:img.owlet {:src "img/owlet-logo.png"}]]
     [:div.menu
      [:h1 "owlet"]
      [:ul.nav.nav-pills.nav-stacked
       [:li.nav-item
        [:a.nav-link {:href "/#/"}
         [:img {:src "img/icon1.png"}]]]
       [:li.nav-item
        [:a.nav-link {:href "/#/"}
         [:img {:src "img/icon2.png"}]]]
       [:li.nav-item
        [:a.nav-link {:href "/#/settings"}
         [:img {:src "img/icon3.png"}]]]]]])
