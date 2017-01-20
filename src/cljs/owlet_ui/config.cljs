(ns owlet-ui.config
  (:require [cljsjs.auth0-lock :as Auth0Lock]
            [re-frame.core :as re-frame]))

(def debug?
  ^boolean js/goog.DEBUG)

(defonce project-name "OWLET")

(defonce auth0-local-storage-key "owlet:user-token")

(defonce server-url "https://owlet-api.herokuapp.com")

(defonce default-header-bg-image "img/default_background.png")

(defonce lock
  (new js/Auth0Lock
       "aCHybcxZ3qE6nWta60psS0An1jHUlgMm"
       "codefordenver.auth0.com"
       (clj->js {:auth {:redirect false
                        :responseType "token"}})))

(defonce owlet-activities-2-space-id "ir2v150dybog")

(defonce firebase-app-init
         {:apiKey        "AIzaSyAbs6wXxPGX-8XEWR6nyj7iCETOL6dZjzY"
          :authDomain    "owlet-users.firebaseapp.com"
          :databaseURL   "https://owlet-users.firebaseio.com"
          :storageBucket "owlet-users.appspot.com"})

(.on lock "authenticated"
     (fn [auth-res]
       (let [_auth-res_ (js->clj auth-res :keywordize-keys true)
             token (:idToken _auth-res_)
             social-id (-> _auth-res_ :idTokenPayload :sub)]
         (re-frame/dispatch [:user-has-logged-in-out! true])
         (re-frame/dispatch [:update-sid-and-get-cms-entries-for social-id])
         (.setItem js/localStorage auth0-local-storage-key token))))
