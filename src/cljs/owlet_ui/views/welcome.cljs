(ns owlet-ui.views.welcome
  (:require
    [owlet-ui.components.login :refer [login-component]]
    [re-frame.core :as rf]))

(defn welcome-view []
  [:div.flexcontainer {:style {:height "100vh"}}
    [:div.landing]
    [:div.column-left]
    [:div.column-right
          [:div.user-type
            [:p#largetext.text-shadow "Welcome to Owlet"]
            [:div.welcome-text.text-shadow
              [:p "Explore some of the awesome things you can do with multimedia & coding!"]
              [:p "¡Bienvenidx a Owlet! Explora algunas de las cosas increíbles que puedes hacer con programación y con multimedios. ¡Adelante!"]]
            [:a {:href "#/branches"}
              [:button.btn.btn-branches "Go to Activities"]]]]
    [:div.login-landing
      [login-component]]
    [:div.flex-item]
    [:div.flex-item
        [:img {:src "img/landing.png"
               :width "100%"}]]
    [:div.flex-item]])
