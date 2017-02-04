(ns owlet-ui.components.search-bar
  (:require [re-com.core :refer [typeahead]]
            [re-frame.core :as rf]
            [reagent.core :as reagent]))

(defn search-bar []
  (let [search-model (reagent/atom {})
        branches (rf/subscribe [:activity-branches])
        skills (rf/subscribe [:skills])
        result-formatter #(-> {:term % :active-view @(rf/subscribe [:active-view])})
        ;; simulate a search feature by scanning the baked-in constant collection
        suggestions-for-search
        (fn [s]
          (into []
                (take 16
                      (for [n (concat @skills @branches)
                            :when (re-find (re-pattern (str "(?i)" s)) n)]
                        (result-formatter n)))))
        change-handler (fn [{:keys [term]}]
                         (prn term))]
    [:div.search-bar-wrap
     [typeahead
      :width "100%"
      :class "form-control"
      :on-change change-handler
      :suggestion-to-string #(:term %)
      :debounce-delay 100
      :change-on-blur? true
      :rigid? true
      :data-source suggestions-for-search
      :model search-model
      :placeholder "Search..."
      :render-suggestion (fn [{:keys [term]}] term)]]))
