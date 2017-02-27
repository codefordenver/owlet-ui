(ns owlet-ui.components.lpsidebar
  (:require [owlet-ui.components.login :refer [login-component]]
            [reagent.core :as reagent]
            [owlet-ui.helpers :refer [class-names]]
            [re-frame.core :as rf]))

(defonce toggle-classes (reagent/atom
                          (set ["lpsidebar-overlay" "lpsidebar-close" "hidden-md-up"])))

(defn lpsidebar-component []
  (if @(rf/subscribe [:open-sidebar?])
    (do
      (swap! toggle-classes disj "closed-sidebar")
      (swap! toggle-classes conj "opened-sidebar"))
    (do
      (swap! toggle-classes conj "closed-sidebar")
      (swap! toggle-classes disj "opened-sidebar")))
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
       [:img {:src "img/icon1.png"}]]]]]
   [:img.lpsidebar-open.hidden-md-up {:src      "img/owlet-tab-closed.png"
                                      :on-click #(rf/dispatch [:set-sidebar-state true])}]
   [:img.lpsidebar-close.hidden-md-up {:src      "img/owlet-tab-opened.png"
                                       :on-click #(rf/dispatch [:set-sidebar-state false])}]])
