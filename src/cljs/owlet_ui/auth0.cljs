(ns owlet-ui.auth0
  (:require [re-frame.core :as re]
            [owlet-ui.config :refer [auth0-init auth0-delegation-options]]
            [cljsjs.auth0-lock]))


(def instance
  (js/Auth0. (clj->js auth0-init)))


(def lock
  (js/Auth0Lock.
    (:clientID auth0-init)
    (:domain auth0-init)
    (clj->js {:auth {:connectionScopes {:google-oauth2 ["openid" "profile"]}}})))


;           [token     (:idToken auth-result)
;            social-id (-> auth-result :idTokenPayload :sub)]
;        (re-frame/dispatch [:user-has-logged-in-out! true])
;        (re-frame/dispatch [:update-sid-and-get-cms-entries-for social-id])
;        (.setItem js/localStorage "userToken" token)


(defn on-authenticated
  "Registers a re-frame event to be fired whenever the user signs in. The event
  is a vector whose first element is the given event id (typically a keyword),
  followed by the Auth0 delegation result, followed by the given arguments, if
  any. The the value associated with :id_token in the delegation result will be
  the token returned to the Auth0 server from the authentication provider, e.g.
  Google.
  "
  [lock-obj event-id & args]
  (.on lock-obj
       "authenticated"
       (fn [auth-result]
         (let [options (assoc auth0-delegation-options
                         :id_token (aget auth-result "idToken"))]
           (.getDelegationToken
             instance
             (clj->js options)
             (fn [err delegation-result]
               (let [del-rslt-as-clj (js->clj
                                       delegation-result :keywordize-keys true)]
                 (re/dispatch
                   (apply vector event-id del-rslt-as-clj args)))))))))

