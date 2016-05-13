(ns owlet.components.sidebar
  (:require
    [reagent.core :refer [atom]]))

(defn sidebar-component []
      [:div#sidebar
       [:a {:href "#/"}
        [:img.owlet {:src "img/owlet-logo.png"}]]
       ;; TODO: fix responsive menu
       [:div.menu
        [:span.hidden-md-up
         [:button.btn-primary.btn-md {:type        "button"
                                      :data-toggle "offcanvas"
                                      :on-click    #(-> (js/$ ".row-offcanvas")
                                                        (.toggleClass "active"))} "Menu"]]
        [:h1 "owlet"]
        [:ul.nav.nav-pills.nav-stacked
         [:li.nav-item
          [:a.nav-link {:href "#/"}
           [:img {:src "img/icon1.png"}]]]
         [:li.nav-item
          [:a.nav-link {:href "#/"}
           [:img {:src "img/icon2.png"}]]]
         [:li.nav-item
          [:a.nav-link {:href "#/settings"}
           [:img {:src "img/icon3.png"}]]]]]])
