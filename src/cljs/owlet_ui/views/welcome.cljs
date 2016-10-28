(ns owlet-ui.views.welcome
  (:require
    [owlet-ui.components.login :refer [login-component]]))

(defn welcome-view []
      [:div.flexcontainer {:style {:height "100vh"}}
        [:div.landing]
        [:div.column-left]
        [:div.column-right
          [:form.user-type
            [:input.user-type {:type "radio"
                               :name "userType"
                               :value "student"
                               :id "student"}]
            [:label {:for "student"}
              "Student"]
            [:input.user-type {:type "radio"
                               :name "userType"
                               :value "teacher"
                               :id "teacher"}]
            [:label {:for "teacher"}
              "Teacher"]]]
        [:div.login
          [login-component]]
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
