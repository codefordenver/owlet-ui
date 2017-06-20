(ns owlet-ui.components.email-notification
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]
            [ajax.core :as ajax :refer [GET POST PUT]]
            [owlet-ui.config :as config]))

(defonce email-endpoint
         (str config/server-url
              "/owlet/webhook/content/subscribe"))

(def res (reagent/atom nil))

(defn subscribe [email]
  (PUT email-endpoint {:params {:email email}
                       :format :json
                       :handler #(reset! res true)
                       :error-handler #(reset! res false)}))

(defn email-notification []
  (let [email (reagent/atom nil)]
    (fn []
      [:div#email-wrap
       [:p#email-text "Notify me when new activities are added"]
       (cond
         (= @res true) [:p {:style {:color "green"}} "Success! You are now subscribed."]
         (= @res false) [:p {:style {:color "red"}} "Unsuccessful. Please try again."])
       [:input#email-input {:type "text"
                            :placeholder "Email address"
                            :on-change #(reset! email (-> % .-target .-value))
                            :value @email}]
       [:button#email-button {:on-click #(subscribe @email)}
         "Subscribe"]])))
