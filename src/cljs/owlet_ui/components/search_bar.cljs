(ns owlet-ui.components.search-bar
  (:require [re-com.core :refer [typeahead]]
            [re-frame.core :as rf]
            [reagent.core :as reagent]))

(defn suggestion-render [{:keys [search-term]}]
  [:span {:style {:color "white"
                  :background "black"}} search-term])

(defn search-bar []
  (let [search-model (reagent/atom {})
        branches (rf/subscribe [:activity-branches])
        skills (rf/subscribe [:skills])
        result-formatter #(-> {:search-term % :current-view @(rf/subscribe [:active-view])})
        ;; simulate a search feature by scanning the baked-in constant collection
        suggestions-for-search
        (fn [s]
          (into []
                (take 16
                      (for [n (concat @skills @branches)
                            :when (re-find (re-pattern (str "(?i)" s)) n)]
                        (result-formatter n)))))]
    [:div.search-bar-wrap
     [typeahead
      :width "100%"
      :class "form-control"
      :suggestion-to-string #(:search-term %)
      :data-source suggestions-for-search
      :model search-model
      :placeholder "Search..."
      :render-suggestion suggestion-render]
     [:img#search-icon {:src "img/search.png"}]]))
