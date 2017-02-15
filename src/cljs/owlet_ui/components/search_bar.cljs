(ns owlet-ui.components.search-bar
  (:require [owlet-ui.helpers :refer [clean-search-term]]
            [re-com.core :refer [typeahead]]
            [re-frame.core :as rf]
            [reagent.core :as reagent]))

(defonce search-model (reagent/atom {}))

(defonce suggestion-count (reagent/atom 16))

(defn toggle-suggestions [state]
   (let [suggestions (aget (js->clj (js/document.getElementsByClassName "rc-typeahead-suggestions-container")) 0)]
     (when-not (nil? suggestions)
      (prn state)
      (set! (.-hidden suggestions) state))))

(defn search-bar []
  (let [branches (rf/subscribe [:activity-branches])
        skills (rf/subscribe [:skills])
        activity-titles (rf/subscribe [:activity-titles])
        activity-platforms (rf/subscribe [:activity-platforms])
        search-collections (concat @skills @branches @activity-titles @activity-platforms)
        result-formatter #(-> {:term %})
        suggestions-for-search
        (fn [s]
          (if (< 1 (count s))
            (reset! suggestion-count 16)
            (reset! suggestion-count 0))
          (prn @suggestion-count)
          (into []
                (take @suggestion-count
                      (for [n (distinct search-collections)
                            :when (re-find (re-pattern (str "(?i)" s)) n)]
                        (result-formatter n)))))
        change-handler #(rf/dispatch [:filter-activities-by-search-term (:term %)])]
    [:div.search-bar-wrap {:on-blur #(toggle-suggestions true)
                           :on-focus #(toggle-suggestions false)}
     [typeahead
      :width "100%"
      :on-change change-handler
      :suggestion-to-string #(:term %)
      :debounce-delay 100
      :change-on-blur? true
      :rigid? true
      :data-source suggestions-for-search
      :model search-model
      :placeholder "Search..."
      :render-suggestion #(:term %)]]))
