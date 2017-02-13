(ns owlet-ui.views.search-results
  (:require [re-frame.core :as rf]
            [owlet-ui.components.activity-thumbnail :refer [activity-thumbnail]]
            [owlet-ui.components.back :refer [back]]))

;; NOTE: (zaden) might not need this view but lets keep it around
;; in case we find that branch-activities doesnt really fit our needs..

(defn search-results-view []
  ;; NOTE: (zaden) returning empty vector throws error,
  ;; so lets return this empty :div for now
  [:div])
