(ns owlet-devcards.app
  (:require [devcards.core]
            [reagent.core :as reagent]
            [owlet-ui.views.tracks :refer [tracks-view]]
            [owlet-ui.components.sidebar :refer [sidebar-component]])
  (:require-macros [devcards.core :as dc
                    :refer [defcard defcard-rg]]))

(enable-console-print!)

(defcard-rg tracks-view
            "**tracks view**"
            [tracks-view])

(defcard-rg sidebar-component
            "**our sidebar component**"
            [sidebar-component])

(defn init []
      (dc/start-devcard-ui!))
