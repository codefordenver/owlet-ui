(ns owlet.components.email-notification
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]
            [ajax.core :as ajax :refer [GET POST PUT]]
            [owlet.config :as config]))

(defonce email-endpoint
         (str config/server-url
              "/owlet/webhook/content/subscribe"))

(def res (reagent/atom nil))

(def msg (reagent/atom false))

(def email-input-state (reagent/atom false))

(add-watch res :watcher (fn [key atom old-state new-state]
                          (reset! msg false)
                          (js/setTimeout #(reset! msg true) 100)))

(defn subscription-response [response]
  (cond

    (or (= "Re-sent confirmation email." response)
        (= "Sent confirmation email." response)) (reset! res 2)

    (= "Already subscribed." response) (reset! res 1)

    :else (reset! res 0)))

(defn subscribe [email]
  (PUT email-endpoint {:params        {:email email}
                       :format        :json
                       :handler       subscription-response
                       :error-handler #(reset! res 0)}))

(defn email-notification []
  (let [email (reagent/atom nil)]
    (fn []
      (if @email-input-state
        [:div#email-wrap
          [:p#email-text-opened {:on-click #(reset! email-input-state false)} "Notify me when new activities are added -"]
          (when @msg
            (cond
              (= @res 2) [:p.refresh {:style {:color "green"}} "Almost there! Check your email to confirm."]
              (= @res 1) [:p.refresh {:style {:color "yellow"}} "You are already subscribed."]
              (= @res 0) [:p.refresh {:style {:color "red"}} "Unsuccessful. Please try again."]))
          [:input#email-input {:type        "text"
                               :placeholder "Email address"
                               :on-change   #(reset! email (-> % .-target .-value))
                               :value       @email}]
          [:button#email-button {:on-click #(subscribe @email)}
            "Subscribe"]]
        [:div#email-wrap
          [:p#email-text-closed {:on-click #(reset! email-input-state true)} "Notify me when new activities are added +"]]))))
