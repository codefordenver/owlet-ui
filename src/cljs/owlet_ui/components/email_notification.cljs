(ns owlet-ui.components.email-notification
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]))

(defn email-notification []
  [:div#email-wrap
    [:p#email-text "Notify me when new activities are added"]
    [:input#email-input {:type "text"
                         :placeholder "Email address"}]
    [:button#email-button "Subscribe"]])
