(ns owlet-ui.views.activity
  (:require [re-frame.core :as re]
            [reagent.core :as reagent :refer [atom]]
            [owlet-ui.components.activity.title :refer [activity-title]]))

(defn activity-view []
  (fn []
    [:div
      [activity-title]]))
