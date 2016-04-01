(ns owlet.app
  (:require [reagent.core :as reagent :refer [atom]]
            [cljsjs.auth0-lock :as Auth0Lock]
            [ajax.core :refer [PUT]]))

(enable-console-print!)

(def user-id (atom nil))

(def lock (new js/Auth0Lock
               "aCHybcxZ3qE6nWta60psS0An1jHUlgMm"
               "codefordenver.auth0.com"))

(defn sign-in-out-component []
      (let [is-logged-in? (atom false)
            _ (if (not (nil? (.getItem js/localStorage "userToken")))
                (swap! is-logged-in? not))]
           (fn []
               [:div.pull-right
                [:button.btn.btn-success.btn-lg
                 {:type    "button"
                  :onClick (fn [_]
                               (if-not @is-logged-in?
                                       (.show lock #js {:popup true}
                                              (fn [err profile token]
                                                  (if (not (nil? err))
                                                    (print err)
                                                    (do
                                                      (reset! user-id (.-user_id profile))
                                                      (swap! is-logged-in? not)
                                                      (.log js/console @user-id)
                                                      ;; save the JWT token
                                                      (.setItem js/localStorage "userToken" token)))))
                                       (do
                                         (swap! is-logged-in? not)
                                         (.removeItem js/localStorage "userToken")))
                               )} (if @is-logged-in? "log-out"
                                                     "log-in")]])))

(defn custom-header-component []
      (let [img-src (atom "http://eskipaper.com/images/space-1.jpg")]
           (fn []
               [:div.custom-header.no-gutter
                [:button.btn-primary
                 {:onClick
                  (fn []
                      (let [url (js/prompt "i need a url")]
                           (when url
                                 (do
                                   (.setItem js/localStorage "custom-image-url" url)
                                   (reset! img-src url)))
                           ))} "change me!"]
                [:img {:src @img-src}]])))

(defn settings []
      (let [district-id (atom nil)]
           (fn []
               [:label "id"
                [:input.test {:type      "text"
                              :value     @district-id
                              :on-change #(reset! district-id (-> % .-target .-value))}]
                [:input {:type     "submit"
                         :value    "Search"
                         :on-click (fn []
                                       ;; "http://owlet-cms.apps.aterial.org/api/users"
                                       (PUT "http://localhost:3000/api/users-district-id"
                                            {:params  {:district-id @district-id
                                                       :user-id     @user-id}
                                             :handler (fn [res]
                                                          (js/alert res))}))}]])))

(defn main []
      [:div.no-gutter
       [:div.left.col-lg-2.text-center
        [:img {:src "img/owlet-logo.png"}]
        [:div.options
         [:h1 "owlet"]
         [:img {:src "img/icon1.png"}] [:br]
         [:img {:src "img/icon2.png"}] [:br]
         [:img {:src "img/icon3.png"}]]]
       [:div.right.col-lg-10
        [custom-header-component]
        [:div.search
         [sign-in-out-component]
         [:input {
                  :type "search"
                  :name "sitesearch"}]
         [:input {:type  "submit"
                  :value "Search"}]]
        [:div.content
         [settings]]]])

(defn init []
      (reagent/render-component
        [main]
        (.getElementById js/document "mount")))
