(ns owlet-ui.components.search-bar
  (:require [owlet-ui.helpers :refer [clean-search-term]]
            [re-com.core :refer [typeahead]]
            [re-frame.core :as rf]
            [reagent.core :as reagent]))

(defonce search-model (reagent/atom {}))

(defonce suggestion-count (reagent/atom 16))

(defonce this-scroll (atom 0))

(defonce last-scroll (atom 0))

(defn toggle-suggestions []
  (if-let [suggestions (aget (js->clj (js/document.getElementsByClassName "rc-typeahead-suggestions-container")) 0)]
    (let [hidden (.-hidden suggestions)]
      (when-not (nil? suggestions)
        (set! (.-hidden suggestions) (not hidden))))))

(defn show-search []
  (let [search (aget (js->clj (js/document.getElementsByClassName "form-control")) 0)]
    (set! (-> search .-style .-height) "50px")
    (set! (.-placeholder search) "Search...")
    (reset! last-scroll @this-scroll)))

(defn hide-search []
  (let [search (aget (js->clj (js/document.getElementsByClassName "form-control")) 0)]
    (set! (-> search .-style .-height) "0px")
    (set! (.-placeholder search) "")
    (.blur search)
    (reset! last-scroll @this-scroll)))

(defn check-scroll []
  (let [content (aget (js->clj (js/document.getElementsByClassName "content")) 0)]
    (reset! this-scroll (-> content .-scrollTop))
    (when (>= (- @this-scroll @last-scroll) 50)
      (hide-search))
    (when (<= (- @this-scroll @last-scroll) -50)
      (show-search))))

(defn search-bar []
  (reagent/create-class
    {:component-did-mount
      (fn []
        (let [content (aget (js->clj (js/document.getElementsByClassName "content")) 0)]
          (js/document.addEventListener "scroll" content #(check-scroll))))
     :reagent-render
      (fn []
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
                (into []
                      (take @suggestion-count
                            (for [n (distinct search-collections)
                                  :when (re-find (re-pattern (str "(?i)" s)) n)]
                              (result-formatter n)))))
              change-handler #(rf/dispatch [:filter-activities-by-search-term (:term %)])]
          [:div.search-bar-wrap {:on-blur #(toggle-suggestions)
                                 :on-focus #(toggle-suggestions)
                                 :on-click #(show-search)}
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
            :render-suggestion #(:term %)]]))}))
