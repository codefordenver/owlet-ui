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
         [:div.no-gutter
          [:div.container-fluid
           [:div.row.row-offcanvas.row-offcanvas-left
            [sidebar-component]
            [:div.col-md-9.col-lg-10
             [header-component]
             [:span.hidden-md-up
               [:button.btn-primary.btn-md {:type "button"
                                            :data-toggle "offcanvas"
                                            :onClick
                                              (fn []
                                                (-> (js/$ ".row-offcanvas")
                                                  (.toggleClass "active")))} "Menu"]]
             [:div.login
              [login-component]]
             [:div.search.pull-right
              [:input {:type "search"
                       :name "sitesearch"}]
              [:input {:type "submit"
                       :value "\uD83D\uDD0D"}]]
          [:div.content
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
                                                          (js/alert res))}))}]]]]]]]])))
