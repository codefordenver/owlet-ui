(ns owlet.app
  (:require [reagent.core :as reagent :refer [atom]]
            [cljsjs.auth0-lock :as Auth0Lock]))

(enable-console-print!)

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
                                                      (print profile)
                                                      (swap! is-logged-in? not)
                                                      ;; save the JWT token.
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
         [:input {
                  :type  "submit"
                  :value "Search"}]]]
       [:div.content]])

(defn init []
      (reagent/render-component
        [main]
        (.getElementById js/document "mount")))
