(ns owlet-ui.components.search-bar)

(defn search-bar []
  [:div.search-bar-wrap
    [:input.form-control {:type "search"
                          :placeholder "Search..."}]
    [:img#search-icon {:src "img/search.png"}]])
