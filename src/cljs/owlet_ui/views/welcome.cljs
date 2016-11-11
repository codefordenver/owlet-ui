(ns owlet-ui.views.welcome
  (:require
    [owlet-ui.components.login :refer [login-component]]))

(defn welcome-view []
  [:div.flexcontainer {:style {:height "100vh"}}
    [:div.landing]
    [:div.column-left]
    [:div.column-right
;;      [:form.user-type
          [:div.user-type
            [:p#largetext "Welcome to Owlet"]
            [:a {:href "#/tracks"}
              [:button.btn.btn-tracks "Go to Tracks"]]]]
;;          [:p#smalltext "To get started, select what type of user you are"]]
;;        [:input.user-type {:type "radio"
;;                           :name "userType"
;;                           :value "student"
;;                           :id "student"
;;                           :defaultChecked true]]
;;        [:label {:for "student"}
;;          "Student"]
;;        [:input.user-type {:type "radio"
;;                           :name "userType"
;;                           :value "teacher"
;;                           :id "teacher"]]
;;        [:label {:for "teacher"}
;;          "Teacher"]]]
    [:div.login-landing
      [login-component]]
    [:div.flex-item]
    [:div.flex-item
        [:img {:src "img/landing.png"
               :width "100%"}]]
    [:div.flex-item]])

    ;;[:div.search.pull-right]
    ;;[:input {:type "search"
    ;;         :name "sitesearch"}
    ;;[:input {:type  "submit"
    ;;         :value "\uD83D\uDD0D"}]])
