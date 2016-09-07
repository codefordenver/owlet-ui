(ns owlet-ui.components.sidebar
  (:require
    [cljsjs.jquery]
    [reagent.core :refer [atom]]))

(defn sidebar-component []
  [:div
    [:div#lpsidebar.hidden-sm-up
      [:div.menu
        [:a {:href "#/library"}
          [:button.btn-primary {:type "button"} "Library"]]
        [:a {:href "#/"}
          [:button.btn-primary {:type "button"} "Home"]]
        [:a {:href "#/settings"}
          [:button.btn-primary {:type "button"} "Settings"]]]]

    [:div#sidebar.hidden-xs-down
      [:a {:href "#/"}
        [:img.owlet {:src "img/owlet-logo.png"}]]
      [:div.menu
        [:span
          [:h1 "owlet"]
          [:ul.nav.nav-pills.nav-stacked
            [:li.nav-item
              [:a.nav-link {:href "#/library"}
                [:img {:src "img/icon1.png"}]]]
            [:li.nav-item
              [:a.nav-link {:href "#/"}
                [:img {:src "img/icon2.png"}]]]
            [:li.nav-item
              [:a.nav-link {:href "#/settings"}
                [:img {:src "img/icon3.png"}]]]]]]]])
