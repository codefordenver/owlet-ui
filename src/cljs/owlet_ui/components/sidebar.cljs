(ns owlet-ui.components.sidebar
  (:require [owlet-ui.components.login :refer [login-component]]
            [re-frame.core :as rf]
            [re-com.core :as re-com :refer-macros [handler-fn]]
            [re-com.popover]
            [reagent.core :as reagent]))

(defn sidebar-component []
  (let [showing? (reagent/atom false)]
    [:div#sidebar
     [:div#owlet-logo-div
      [:a#owlet-image {:href "#"}
        [:img#owlet-owl {:src "../img/owlet-owl.png"}]]]
     [:div.menu
      [:div.login
       [login-component]]
      [:a.navigation {:href "#/branches"
                      :on-click #(rf/dispatch [:set-active-view :branches-view])}
       [re-com/popover-anchor-wrapper
         :showing? showing?
         :position :right-center
         :anchor [:div.branch-icon
                  {:on-mouse-over (handler-fn (reset! showing? true))
                   :on-mouse-out  (handler-fn (reset! showing? true))}]
         :popover [re-com/popover-content-wrapper
                   :close-button? false
                   :body "Go to activities"]]]]]))
