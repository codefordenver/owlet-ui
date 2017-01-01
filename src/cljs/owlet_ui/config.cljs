(ns owlet-ui.config
  (:require [cljsjs.auth0-lock :as Auth0Lock]
            [re-frame.core :as re-frame]))

(def debug?
  ^boolean js/goog.DEBUG)

;; Use a lein env var like the one above
;; to toggle this during development
;; http://localhost:3000
(def server-url "https://owlet-api.herokuapp.com")

(when debug?
  (enable-console-print!))

(def lock
  (new js/Auth0Lock
       "aCHybcxZ3qE6nWta60psS0An1jHUlgMm"
       "codefordenver.auth0.com"
       (clj->js {:auth {:redirect false
                        :responseType "token"}})))

(.on lock "authenticated"
     (fn [auth-res]
       (let [_auth-res_ (js->clj auth-res :keywordize-keys true)
             token (:idToken _auth-res_)
             social-id (-> _auth-res_ :idTokenPayload :sub)]
         (re-frame/dispatch [:user-has-logged-in-out! true])
         (re-frame/dispatch [:update-sid-and-get-cms-entries-for social-id])
         (.setItem js/localStorage "owlet:user-token" token))))

(def default-header-bg-image
  "http://apod.nasa.gov/apod/image/1607/OrionNebula_ESO_4000.jpg")

(defonce owlet-activities-2-space-id "ir2v150dybog")

(defonce firebase-app-init
         {:apiKey        "AIzaSyAbs6wXxPGX-8XEWR6nyj7iCETOL6dZjzY"
          :authDomain    "owlet-users.firebaseapp.com"
          :databaseURL   "https://owlet-users.firebaseio.com"
          :storageBucket "owlet-users.appspot.com"})
