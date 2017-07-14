(ns owlet.components.search-bar
  (:require [owlet.helpers :refer [clean-search-term]]
            [re-com.core :refer [typeahead]]
            [re-frame.core :as rf]
            [reagent.core :as reagent]
            [clojure.string :as string]
            [owlet.helpers :refer [class-names]]
            [camel-snake-kebab.core :refer [->kebab-case]]))

(defonce search-model (reagent/atom {}))

(defonce suggestion-count (reagent/atom 16))

(defonce search-classes
  (reagent/atom #{"search"}))

(def scroll-delta (atom `(0 0)))

(def swap-scroll (comp (partial drop-last) conj))

(defn toggle-suggestions []
  (if-let [suggestions (aget (js->clj (js/document.getElementsByClassName "rc-typeahead-suggestions-container")) 0)]
    (let [hidden (.-hidden suggestions)]
      (when-not (nil? suggestions)
        (set! (.-hidden suggestions) (not hidden))))))

(defn push-scroll [collection n]
  (conj (drop 1 collection) n))

(defn update-scroll! [n]
  (swap! scroll-delta push-scroll n))

(defn change-scroll! [n]
  (swap! scroll-delta swap-scroll n))

(defn check-scroll [contentNodeRef]
  (update-scroll! (.-scrollTop contentNodeRef))
  (let [delta (apply - @scroll-delta)
        search (aget (js->clj (js/document.getElementsByClassName "form-control")) 0)]
    (when (>= delta 50)
      (do
        (swap! search-classes conj "hidden-search")
        (.blur search)
        (change-scroll! (.-scrollTop contentNodeRef))))
    (when (<= delta -50)
        (swap! search-classes disj "hidden-search")
        (change-scroll! (.-scrollTop contentNodeRef)))))

(defn search-bar []
  (reagent/create-class
    {:component-did-mount
      (fn []
        (let [contentNodeRef (aget (js->clj (js/document.getElementsByClassName "content")) 0)]
          (.addEventListener contentNodeRef "scroll" #(check-scroll contentNodeRef))))
     :reagent-render
      (fn []
        (let [branches (rf/subscribe [:activity-branches])
              skills (rf/subscribe [:skills])
              activity-titles (rf/subscribe [:activity-titles])
              activity-platforms (rf/subscribe [:activity-platforms])
              platform-names (map #(:name %) @activity-platforms)
              search-collections (concat @skills @branches @activity-titles platform-names)
              result-formatter #(-> {:term %})
              suggestion-renderer #(:term %)
              special-char-pattern (re-pattern "[^A-Za-z0-9]")
              suggestions-for-search
              (fn [s]
                (if (< 1 (count s))
                  (reset! suggestion-count 16)
                  (reset! suggestion-count 0))
                (into []
                      (take @suggestion-count
                            (for [n (distinct search-collections)
                                  :when (re-find (re-pattern (str "(?i)" (string/replace s special-char-pattern #(str "\u005C" %)))) n)]
                              (result-formatter n)))))
              change-handler (fn [t]
                               (let [platform-search-names (map #(->kebab-case (:name %)) @activity-platforms)
                                     platform-names (map #(:name %) @activity-platforms)
                                     platform-index (.indexOf platform-names (:term t))]
                                 (if (>= platform-index 0)
                                   (rf/dispatch [:filter-activities-by-search-term (nth platform-search-names platform-index)])
                                   (rf/dispatch [:filter-activities-by-search-term (:term t)]))))]
          [:div.search-bar-wrap {:on-blur #(toggle-suggestions)
                                 :on-focus #(toggle-suggestions)
                                 :on-click #(swap! search-classes disj "hidden-search")}
           [typeahead
            :class (class-names @search-classes)
            :width "100%"
            :on-change change-handler
            :suggestion-to-string suggestion-renderer
            :debounce-delay 100
            :change-on-blur? true
            :rigid? true
            :data-source suggestions-for-search
            :model search-model
            :placeholder "Search..."
            :render-suggestion suggestion-renderer]]))}))
