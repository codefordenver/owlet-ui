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
      [:div#email-wrap {:style {:background-color (cond
                                                    (= @res true) "green"
                                                    (= @res false) "red")}}
       (cond
         (= @res true) "Success"
         (= @res false) "Failed"
         :else
           [:div
            [:p#email-text "Notify me when new activities are added"]
            [:input#email-input {:type "text"
                                 :placeholder "Email address"
                                 :on-change #(reset! email (-> % .-target .-value))
                                 :value @email}]
            [:button#email-button {:on-click #(subscribe @email)}
              "Subscribe"]])])))
