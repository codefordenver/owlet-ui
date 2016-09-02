(ns owlet-ui.components.sidebar
  (:require
    [cljsjs.jquery]
    [reagent.core :refer [atom]]))

(defn sidebar-component []
        [:div
          [:span
            [:button.btn-primary.btn-md {:type        "button"}
                                        :data-toggle "offcanvas"
                                        :on-click    #(-> (js/$ ".row-offcanvas")
                                                          (.toggleClass "active")) "Menu"]]]
        [:div#sidebar.hidden-xs-down
          [:a {:href "#/"}
            [:img.owlet {:src "img/owlet-logo.png"}]]
          ;; TODO: fix responsive menu
          [:div.menu
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
                  [:img {:src "img/icon3.png"}]]]]]])
