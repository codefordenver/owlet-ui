(ns owlet-ui.helpers
  (:require [clojure.string :as clj->str]
            [camel-snake-kebab.core :refer [->kebab-case]]
            [cljsjs.showdown]))

(defn keywordize-name [name]
  (-> name ->kebab-case keyword))

(def remove-nil (partial remove nil?))

(defn parse-platform [term]
  (if (not (nil? term))
    (let [name (re-find #"\[+(.*)(\]+)" term)]
      (if (seq name)
        (second name)
        term))
    ""))


(defn clean-search-term [term]
  (clj->str/trim
    (clj->str/replace term #"(\*)" "")))

;; for parsing markdown with showdown
(def showdown (js/showdown.Converter.))
