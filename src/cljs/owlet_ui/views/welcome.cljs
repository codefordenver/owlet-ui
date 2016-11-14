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
            [:div.welcome-text
              [:p "Explore some of the awesome things you can do with coding & multimedia, by yourself and with others. Enjoy!"]
              [:p "¡Bienvenidx a Owlet! Explora algunas de las cosas increíbles que puedes hacer con programación y con multimedios tú solx o en equipo. ¡Adelante!"]]
            [:a {:href "#/tracks"}
              [:button.btn.btn-tracks "Go to Activities"]]]]
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
