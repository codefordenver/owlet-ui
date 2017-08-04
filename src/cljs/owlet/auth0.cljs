(ns owlet.auth0
  (:require [re-frame.core :as rf]
            [owlet.config :refer [auth0-init]]
            [cljsjs.auth0-lock]))


(def auth0-instance
  (js/Auth0. (clj->js auth0-init)))


(def lock
  "A JS object through which we communicate with the Auth0 authentication
  server. Also, its .show method puts up a nice GUI for the user to enter
  login credentials. See

  "
  (js/Auth0Lock.
    (:clientID auth0-init)
    (:domain auth0-init)
    ; Here are configuration options for Auth0Lock. See
    ; https://auth0.com/docs/libraries/lock/v10/customization
    (clj->js {:auth {:connectionScopes {:google-oauth2 ["openid" "profile"]}
                     :redirect         false}})))


(defn on-authenticated
  "Registers a re-frame event to be fired whenever the user signs in. The event
  is a vector whose first element is the given event id (typically a keyword),
  followed by a map containing the Auth0 id token (:id-token) and the
  delegation token (:delegation-token), followed by the given arguments, if
  any. The delegation token will be the token returned to the Auth0 server
  from the authentication provider, e.g. Google.
  "
  [lock-obj auth0-delegation-options event-id error-event-id & args]
  (.on lock-obj
       "authenticated"
       (fn [auth-result]
         (.hide lock-obj)
         (let [idToken (aget auth-result "idToken")
               options (assoc auth0-delegation-options :id_token idToken)]

           (.getDelegationToken
             auth0-instance
             (clj->js options)
             (fn [err delegation-result]

               (if err
                 ; There was an error, so dispatch to the handler for it.
                 (rf/dispatch [error-event-id {:error              err
                                               :delegation-options options}])

                 ; Finally, this callback dispatches the re-frame event,
                 ; event-id along with the two tokens. Note that while
                 ; "id_token" in options refers to the Auth0 token, "id_token"
                 ; in delegation-result refers to the delegation token.
                 (rf/dispatch
                   (apply vector
                          event-id
                          {:auth0-token       idToken
                           :delegation-token  (aget delegation-result
                                                    "id_token")}
                          args))))))))

  (.on lock-obj
       "authorization_error"
       (fn [err]
         (rf/dispatch [error-event-id err])))

  (.on lock-obj
       "unrecoverable_error"
       (fn [err]
         (rf/dispatch [error-event-id err]))))

