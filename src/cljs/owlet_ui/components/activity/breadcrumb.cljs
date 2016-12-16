(ns owlet-ui.components.activity.breadcrumb
  (:require [re-frame.core :as re]
            [reagent.core :as reagent :refer [atom]]))

(defn get-track-name
  "find the matching track name in activity model data"
  [models model-id]
  (let [match (some #(when (= (:model-id %) model-id)
                       (:name %)) models)]
    match))

(defn breadcrumb [activity-data]
  (let [activity-models (re/subscribe [:library-activity-models])]
    (reagent/create-class
      {:component-will-mount
       #(when (empty? @activity-models)
              (re/dispatch [:get-library-content]))
       :reagent-render
       #(let [models (get-in @activity-models [:models])
              model-id (get-in @activity-data [:sys :contentType :sys :id])
              track-name (get-track-name models model-id)]
          [:div.breadcrumb-wrap
           [:div
            [:a {:href (str "#/branches/" model-id)}
             [:img {:src "img/back.png"}]]]
           [:div
            [:a {:href (str "#/branches/" model-id)}
             [:p track-name]]]])})))
