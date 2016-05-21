(ns owlet.views.welcome)

(defn welcome-view []
      [:div.jumbotron
       [:div.search.pull-right
        [:input {:type "search"
                 :name "sitesearch"}]
        [:input {:type  "submit"
                 :value "\uD83D\uDD0D"}]]
       [:div.container-fluid
        [:div.row
         [:div.col-lg-12
          [:h1 "Welcome"]
          [:p.text-center
           "main content area"]]]]])
