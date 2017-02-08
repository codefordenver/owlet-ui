(ns owlet-ui.helpers
  (:require [camel-snake-kebab.core :refer [->kebab-case]]))

(defn keywordize-name [name]
  (-> name ->kebab-case keyword))

(def without-nils (partial remove nil?))
