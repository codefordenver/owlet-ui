(ns owlet.views.settings
  (:require
    [reagent.core :refer [atom]]
    [reagent.validation :as validation]
    [reagent.session :as session]
    [ajax.core :refer [GET PUT]]))

(defonce server-url "http://localhost:3000")

(defn settings-page []
      (let [district-id (atom nil)]
           (fn []
               [:div.container
                [:div.row
                 [:div.col-lg-12
                  [:div.jumbotron
                   [:h1 "My Settings"]
                   [:div.search
                    [:label "District ID:"
                     [:input.test {:type      "text"
                                   :value     @district-id
                                   :on-change #(reset! district-id (-> % .-target .-value))}]
                     [:input {:type     "submit"
                              :value    "Enter"
                              :on-click #(when (and (validation/has-value? (session/get :user-id))
                                                    (validation/has-value? @district-id))
                                               (PUT (str server-url "/api/users-district-id")
                                                    {:params  {:district-id @district-id
                                                               :user-id     (session/get :user-id)}
                                                     :handler (fn [res]
                                                                  (js/alert res))}))}]]]]]]])))
