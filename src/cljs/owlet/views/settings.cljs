(ns owlet.views.settings
  (:require
    [owlet.components.header :refer [header-component]]
    [owlet.components.login :refer [login-component]]
    [owlet.components.sidebar :refer [sidebar-component]]
    [reagent.core :refer [atom]]
    [reagent.validation :as validation]
    [reagent.session :as session]
    [ajax.core :refer [GET PUT]]))

(defonce server-url "http://localhost:3000")

(defn settings-page []
  (let [district-id (atom nil)]
       (fn []
         [:div#main.container-fluid
          [:div.row.row-offcanvas.row-offcanvas-left
           [sidebar-component]
           [:div.col-md-9.col-lg-10.main
            [header-component]
            [:p.hidden-md-up
              [:button.btn.btn-primary-outline.btn-sm {:type "button"
                                                       :data-toggle "offcanvas"
                                                       :value "Menu"}]]
            [login-component]
            [:div.search
             [:input {:type "search"
                      :name "sitesearch"}]
             [:input {:type  "submit"
                      :value "Search"}]]

            [:label "id"
             [:input.test {:type      "text"
                           :value     @district-id
                           :on-change #(reset! district-id (-> % .-target .-value))}]
             [:input {:type     "submit"
                      :value    "Search"
                      :on-click #(when (and (validation/has-value? (session/get :user-id))
                                            (validation/has-value? @district-id))
                                       (PUT (str server-url "/api/users-district-id")
                                            {:params  {:district-id @district-id
                                                       :user-id     (session/get :user-id)}
                                             :handler (fn [res]
                                                          (js/alert res))}))}]]]]])))
