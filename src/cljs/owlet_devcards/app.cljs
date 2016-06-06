(ns owlet-devcards.app
  (:require [devcards.core]
            [reagent.core :as reagent]
            )
  (:require-macros [devcards.core :as dc
                    :refer [defcard defcard-rg]]))

(enable-console-print!)

(defcard-rg rg-example-2
            "some docs"
            [:div "this works"])

(defn init []
      (dc/start-devcard-ui!))

