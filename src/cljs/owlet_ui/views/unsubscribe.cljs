(ns owlet-ui.views.unsubscribe
  (:require [owlet-ui.components.back :refer [back]]
            [re-frame.core :as rf]
            [reagent.core :as reagent]
            [ajax.core :as ajax :refer [GET POST PUT]]
            [owlet-ui.config :as config]))

(defonce email-endpoint
         (str config/server-url
              "/owlet/webhook/content/unsubscribe"))

(def res (reagent/atom nil))

(def msg (reagent/atom false))

(add-watch res :watcher (fn [key atom old-state new-state]
                          (reset! msg false)
                          (js/setTimeout #(reset! msg true) 100)))

(defn unsubscribe-response [response]
  (cond

    (or (= "Re-sent confirmation email." response)
        (= "Added - awaiting confirmation." response)) (reset! res 2)

    (= "Already Subscribed." response) (reset! res 1)

    :else (reset! res 0)))

(defn unsubscribe [email]
  (PUT email-endpoint {:params        {:email email}
                       :format        :json
                       :handler       unsubscribe-response
                       :error-handler #(reset! res 0)}))

(defn unsubscribe-view []
  (let [email (reagent/atom nil)]
    [:div.not-found
     [:h2 [back]
          [:mark.white.box-shadow "Sorry to see you go"]]
     [:h3 [:mark.white "Enter your email address to unsubscribe"]]
     [:input#email-input {:type        "text"
                          :placeholder "Email address"
                          :on-change   #(reset! email (-> % .-target .-value))
                          :value       @email}]
     [:button#email-button {:on-click #(unsubscribe @email)}
      "Unsubscribe"]]))

;TODO: add email address input form &
     ; a PUT request to update user's :confirmed key to false]])
