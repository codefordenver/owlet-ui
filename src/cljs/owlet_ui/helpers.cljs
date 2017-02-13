(ns owlet-ui.helpers
  (:require [clojure.string :as clj->str]
            [cljs.spec :as s]
            [camel-snake-kebab.core :refer [->kebab-case]]))

(s/def ::string? string?)

(defn check-and-throw
  "throw an exception if value doesn't match the spec"
  [a-spec val]
  (when-not (s/valid? a-spec val)
    (throw (ex-info (str "spec failed because: " (s/explain-str a-spec val)) {}))))

(defn keywordize-name [name]
  (check-and-throw ::string? name)
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
