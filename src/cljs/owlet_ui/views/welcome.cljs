(ns owlet-ui.views.welcome
  (:require
    [owlet-ui.components.login :refer [login-component]]))

(defn welcome-view []
      [:div.flexcontainer {:style {:height "100vh"}}
        [:div.flex-item]
        [:div.flex-item
          [:a {:href "#/tracks"}
            [:img {:src "img/landing.png"
                   :width "100%"}]]]
        [:div.flex-item]])

        ;;[:div.search.pull-right]
        ;;[:input {:type "search"
        ;;         :name "sitesearch"}
        ;;[:input {:type  "submit"
        ;;         :value "\uD83D\uDD0D"}]])
