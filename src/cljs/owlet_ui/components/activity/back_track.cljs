(ns owlet-ui.components.activity.back_track
  (:require [re-frame.core :as re]
            [reagent.core :as reagent :refer [atom]]
            [clojure.string :as str]))

(defn get-track-name
  "find the matching track name in activity model data"
  [models model-id]
  (let [match (some #(when (= (:model-id %) model-id)
                      (:name %)) models)]
      match))

(defn back-track []
  (let [activity-data (re/subscribe [:activity-in-view])
        activity-models (re/subscribe [:library-activity-models])]
    (reagent/create-class
      {:component-will-mount
       #(when (empty? @activity-models)
         (re/dispatch [:get-library-content]))
       :reagent-render
       (fn []
         (let [models (get-in @activity-models [:models])
               model-id (get-in @activity-data [:sys :contentType :sys :id])
               track-name (get-track-name models model-id)]
           [:div.back-track-wrap
            [:h1
              [:a {:href (str "#/tracks/" model-id)} track-name]]]))})))
