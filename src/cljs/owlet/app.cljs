(ns owlet.app
  (:require [reagent.core :as reagent :refer [atom]]
            [cljsjs.auth0-lock :as Auth0Lock]
            [ajax.core :refer [GET PUT]]
            [reagent.session :as session]
            [reagent.validation :as validation]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType])
  (:import goog.History))

(enable-console-print!)

(defonce server-url "http://localhost:3000")                ;; "http://owlet-cms.apps.aterial.org"

(def lock (new js/Auth0Lock
               "aCHybcxZ3qE6nWta60psS0An1jHUlgMm"
               "codefordenver.auth0.com"))

(def user-profile (atom nil))

;; (add-watch user-profile :logger #(-> %4 clj->js js/console.log))

(defn get-user-cms-profile [id]
      (GET (str server-url "/api/user/" id)
           {:response-format :json
            :handler         (fn [res]
                                 (reset! user-profile (clj->js res)))}))

(defn sign-in-out-component []
      (let [is-logged-in? (atom false)
            user-token (.getItem js/localStorage "userToken")
            _ (if user-token
                (.getProfile lock user-token
                             (fn [err profile]
                                 (if (not (nil? err))
                                   (.log js/console err)
                                   (do
                                     (get-user-cms-profile (.-user_id profile))
                                     (swap! is-logged-in? not)
                                     (session/put! :user-id (.-user_id profile)))))))]
           (fn []
               [:div.pull-right
                [:button.btn.btn-success.btn-lg
                 {:type    "button"
                  :onClick #(if-not @is-logged-in?
                                    (.show lock #js {:popup true}
                                           (fn [err profile token]
                                               (if (not (nil? err))
                                                 (print err)
                                                 (do
                                                   (session/put! :user-id (.-user_id profile))
                                                   (swap! is-logged-in? not)
                                                   ;; save the JWT token
                                                   (.setItem js/localStorage "userToken" token)))))
                                    (do
                                      (swap! is-logged-in? not)
                                      (session/clear!)
                                      (.removeItem js/localStorage "userToken")))}
                 (if @is-logged-in? "log-out"
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

(defn settings-page []
      (let [district-id (atom nil)]
           (fn []
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
                                                             (js/alert res))}))}]])))

(defn home-page []
      [:div.no-gutter
       [:div.left.col-lg-2.text-center
        [:img {:src "img/owlet-logo.png"}]
        [:div.options
         [:h1 "owlet"]
         [:img {:src "img/icon1.png"}] [:br]
         [:img {:src "img/icon2.png"}] [:br]
         [:a {:href "/#/settings"}
          [:img {:src "img/icon3.png"}]]]]
       [:div.right.col-lg-10
        [custom-header-component]
        [:div.search
         [sign-in-out-component]
         [:input {
                  :type "search"
                  :name "sitesearch"}]
         [:input {:type  "submit"
                  :value "Search"}]]
        [:div.content]]])

(def pages
  {:home     #'home-page
   :settings #'settings-page})

(defn page []
      [(pages (session/get :page))])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
                    (session/put! :page :home))

(secretary/defroute "/settings" []
                    (session/put! :page :settings))
;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
      (doto (History.)
            (events/listen
              HistoryEventType/NAVIGATE
              (fn [event]
                  (secretary/dispatch! (.-token event))))
            (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-components []
      (reagent/render [#'page]
                      (.getElementById js/document "mount")))

(defn init []
      (hook-browser-navigation!)
      (mount-components))