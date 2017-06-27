(ns owlet-ui.config)


(def debug?
  ^boolean js/goog.DEBUG)
(when debug? (enable-console-print!))


(def project-name "OWLET")

;TODO: change this back to mmmanyfold
(def server-url "http://74d671d9.ngrok.io")


(def auth0-init
  "Credentials for instantiating Auth0 and Auth0Lock objects.
  "
  {:clientID "aCHybcxZ3qE6nWta60psS0An1jHUlgMm"
   :domain   "codefordenver.auth0.com"})


(def auth0-del-opts-for-firebase
  "The options needed by function Auth0.getDelegationToken. Omit key :id_token,
  since it will be provided by owlet-ui.auth0/on-authenticated. See
  https://auth0.com/docs/libraries/auth0js#delegation-token-request
  "
  {:api      "firebase"
   :scope    "openid profile"
   :target   (:clientID auth0-init)})


(defonce firebase-app-init
  ; Used only by owlet-ui.firebase/firebase-app, which may not be redefined
  ; while the app is running. So for clarity, we make this var defonce as well.
  ;
  {:apiKey        "AIzaSyAbs6wXxPGX-8XEWR6nyj7iCETOL6dZjzY"
   :authDomain    "owlet-users.firebaseapp.com"
   :databaseURL   "https://owlet-users.firebaseio.com"
   :storageBucket "owlet-users.appspot.com"})


(def owlet-activities-3-space-id "0okl2i5aeorb")

(def default-header-bg-image "img/default_background.png")
