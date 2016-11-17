(ns owlet-ui.config)


(def debug?
  ^boolean js/goog.DEBUG)

(when debug?
  (enable-console-print!))


;; TODO:
;; Use a lein env var like the one above
;; to toggle this during development
;; http://localhost:3000
(def server-url
  "https://owlet-api.herokuapp.com")


(def auth0-init
  "Credentials for instantiating Auth0 and Auth0Lock objects.
  "
  {:clientID "aCHybcxZ3qE6nWta60psS0An1jHUlgMm"
   :domain   "codefordenver.auth0.com"})


(def auth0-delegation-options
  "The options needed by function Auth0.getDelegationToken. Omit key :id_token,
  since it will be provided by owlet-ui.auth0/on-authenticated. See
  https://auth0.com/docs/libraries/auth0js#delegation-token-request
  "
  {:api      "firebase"
   :scope    "openid profile"
   :target   (:clientID auth0-init)})


(def firebase-app-init
  {:apiKey        "AIzaSyAbs6wXxPGX-8XEWR6nyj7iCETOL6dZjzY"
   :authDomain    "owlet-users.firebaseapp.com"
   :databaseURL   "https://owlet-users.firebaseio.com"
   :storageBucket "owlet-users.appspot.com"})


(def library-space-id "c7i369745nqp")


(def default-header-bg-image
  "http://apod.nasa.gov/apod/image/1607/OrionNebula_ESO_4000.jpg")


(def default-user-db
  "initial user state"
  {:logged-in?                false
   :social-id                 nil
   :content-entries           []
   :background-image          default-header-bg-image
   :background-image-entry-id nil})


(def default-db
  "initial app state"
  {:user                        default-user-db
   :app                         {:loading? nil}
   :activities                  []
   :activity-models             nil
   :activities-by-track-in-view {:track-id nil :display-name nil}
   :activities-by-track         {}
   :activities-in-view          nil
   :activity-in-view            nil})

