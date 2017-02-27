(ns owlet-ui.components.lpsidebar
  (:require [owlet-ui.components.login :refer [login-component]]
            [reagent.core :as reagent]
            [owlet-ui.helpers :refer [class-names]]
            [re-frame.core :as rf]))

(defonce toggle-classes (reagent/atom
                          (set ["lpsidebar-overlay" "lpsidebar-close" "hidden-md-up"])))

(defn lpsidebar-component []
  (if @(rf/subscribe [:open-sidebar?])
    (swap! toggle-classes conj "opened-sidebar")
    (swap! toggle-classes disj "opened-sidebar"))
  [:div
   [:div {:class    (class-names @toggle-classes)
          :on-click #(rf/dispatch [:set-sidebar-state false])}]
   [:div.lpsidebar-wrap.hidden-md-up
    [:div.lpsidebar
     [:div#owlet-logo-div
      [:a#owlet-image {:href "#"} [:img#owlet-owl {:src "../img/owlet-owl.png"}]]]
     [:div.menu
      [:div.login
       [login-component]]
      [:a.navigation {:href     "#/branches"
                      :on-click #(rf/dispatch [:set-active-view :branches-view])}
       [:img {:src "img/icon1.png"}]]]]]])

