(ns owlet-ui.components.lpsidebar
  (:require [owlet-ui.components.login :refer [login-component]]
            [reagent.core :as reagent]
            [owlet-ui.helpers :refer [class-names]]
            [re-frame.core :as rf]))

(def lpsidebar-state (reagent/atom false))

(defn toggle-lpsidebar []
  (swap! lpsidebar-state not))

(defn lpsidebar-component []
    [:div
     (if @lpsidebar-state
      [:div.lpsidebar-overlay.hidden-md-up.opened-sidebar {:on-click #(toggle-lpsidebar)}]
      [:div.lpsidebar-overlay.hidden-md-up])
     [:div.lpsidebar-wrap.hidden-md-up
      [:div.lpsidebar {:style {:width (if @lpsidebar-state
                                        "80px"
                                        "0")}}
       [:div#owlet-logo-div
        [:a#owlet-image {:href "#/"
                         :on-click #(toggle-lpsidebar)}
         [:img#owlet-owl {:src "../img/owlet-owl.png"}]]]
       [:div.menu
        [:div.login {:on-click #(toggle-lpsidebar)}
         [login-component]]
        [:a.navigation {:href     "#/branches"
                        :on-click #(toggle-lpsidebar)}
         [:img {:src "img/icon1.png"}]]]
       (if @lpsidebar-state
         [:img#lpsidebar-opened.lpsidebar-toggle.hidden-md-up {:style {:left (if @lpsidebar-state
                                                                               "80px"
                                                                               "0")}
                                                               :src      "img/owlet-tab-opened.png"
                                                               :on-click #(toggle-lpsidebar)}]
         [:img#lpsidebar-closed.lpsidebar-toggle.hidden-md-up {:style {:left (if @lpsidebar-state
                                                                               "80px"
                                                                               "0")}
                                                               :src      "img/owlet-tab-closed.png"
                                                               :on-click #(toggle-lpsidebar)}])]]])
