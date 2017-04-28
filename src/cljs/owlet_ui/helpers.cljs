(ns owlet-ui.helpers
  (:require [clojure.string :as clj->str]
            [cljs.spec :as s]
            [camel-snake-kebab.core :refer [->kebab-case]]
            [cljsjs.showdown]))

(s/def ::string? string?)

(s/def ::set? set?)

(defn check-and-throw
  "throw an exception if value doesn't match the spec"
  [a-spec val]
  (when-not (s/valid? a-spec val)
    (throw (ex-info (str "spec failed because: " (s/explain-str a-spec val)) {}))))

(defn keywordize-name [name]
  (check-and-throw ::string? name)
  (-> name ->kebab-case keyword))

(def remove-nil (partial remove nil?))

(defn clean-search-term [term]
  (clj->str/trim
    (clj->str/replace term #"(\*)" "")))

;; for parsing markdown with showdown
(def showdown (js/showdown.Converter.))

(defn class-names
  "utility for converting a clojure Set #{a b c}
  to a list of unique CSS classes"
  [cset]
  (check-and-throw ::set? cset)
  (clj->str/join " " (map str cset)))
