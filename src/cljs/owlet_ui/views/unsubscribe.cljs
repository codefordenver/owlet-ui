(ns owlet-ui.views.unsubscribe
  (:require [owlet-ui.components.back :refer [back]]
            [re-frame.core :as rf]))

(defn unsubscribe-view []
  [:div.not-found
   [:h2 [back]
        [:mark.white.box-shadow "Sorry to see you go"]]
   [:h3 [:mark.white "Enter your email address to unsubscribe"]]])

;TODO: add email address input form &
     ; a PUT request to update user's :confirmed key to false]])
