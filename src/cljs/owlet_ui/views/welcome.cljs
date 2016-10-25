(ns owlet-ui.views.welcome)

(defn welcome-view []
      [:div.flexcontainer {:style {:height "100vh"}}
        [:div#top.flex-item]
        [:a#bottom {:href "#/tracks"}
          [:img {:src "img/landing.png"
                 :width "100%"}]]
        [:div#bottom.flex-item]])

        ;;[:div.search.pull-right]
        ;;[:input {:type "search"
        ;;         :name "sitesearch"}
        ;;[:input {:type  "submit"
        ;;         :value "\uD83D\uDD0D"}]])
