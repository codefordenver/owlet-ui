(ns owlet-ui.config
  (:require [cljsjs.auth0-lock :as Auth0Lock]))

(def debug?
  ^boolean js/goog.DEBUG)

;; TODO:
;; Use a lein env var like the one above
;; to toggle this during development
;; http://localhost:3000
(def server-url
  "http://owlet-api.apps.aterial.org")

(when debug?
  (enable-console-print!))

(def lock (new js/Auth0Lock
               "aCHybcxZ3qE6nWta60psS0An1jHUlgMm"
               "codefordenver.auth0.com"))