(ns owlet.events.auth
  (:require [re-frame.core :as rf]
            [owlet.rf-util :refer [reg-setter]]
            [owlet.firebase :as fb]
            [owlet.events.app :as app]))

(reg-setter :my-identity [:my-identity])

(reg-setter :firebase-users-change [:users])


(rf/reg-event-fx
  :log-out
  (fn [cofx _]
    {:db                (app/note-pending cofx :log-out)
     :firebase-sign-out fb/firebase-auth-object}))


(rf/reg-event-fx
  :auth0-authenticated
  (fn [cofx [_ {:keys [auth0-token delegation-token]}]]
    {:firebase-sign-in [fb/firebase-auth-object
                        delegation-token
                        :firebase-sign-in-failed]
     :db               (app/note-pending cofx :log-in)}))


(rf/reg-event-fx
  :auth0-error
  (fn [_ [_ error]]
    (js/console.log "*** Error from Auth0: " error)))


(rf/reg-event-fx
  :firebase-sign-in-failed
  (fn [_ [_ fb-error]]
    (js/console.log "*** Error signing into Firebase: ", fb-error)
    {}))


(defn- id-details
  [fb-id]
  (let [private-ref  (fb/path-str->db-ref (str "users/" (name fb-id)))
        presence-ref (fb/path-str->db-ref (str "presence/" (name fb-id)))]
    {:firebase-id      fb-id
     :private-ref      private-ref
     :presence-ref     presence-ref}))


(rf/reg-event-fx
  :firebase-auth-change
  (fn [cofx [_ fb-user]]
    ; If I'm logged into firebase, fb-user is a JS object containing a string
    ; in its uid property. Otherwise, fb-user is nil. We now record this ID in
    ; subtree :my-identity. Thus, we know I am logged-in simply if and only if
    ; (get-in db [:my-identity :firebase-id]) is not nil. If this value changed
    ; then we also need to turn on/off listening for events about me or of
    ; interest to me, which are dispatched by owlet.firebase/on-change.

    (let [new-fb-id    (some-> fb-user .-uid keyword)
          old-identity (get-in cofx [:db :my-identity])]

      ; Compare my new id with FORMER :firebase-id.
      (if (= new-fb-id (:firebase-id old-identity))
        {}                       ; No change in id. Do nothing.
        (if new-fb-id            ; Id changed. I logged in or out.

          ; I just now logged in.
          (let [new-identity (id-details new-fb-id)]
            {:db (assoc (:db cofx) :my-identity new-identity)
             ; Note that this :my-identity subtree REPLACES the existing one,
             ; if any. So it is important that this takes place BEFORE any
             ; events that modify the subtree, such as produced by the :private
             ; listener registration in :start-authorized-listening, below.
             ; This is ensured here because both the change to app-db and the
             ; registration are effects scheduled for the next tick. So any
             ; event dispatched as the result of the latter cannot preceed the
             ; former.
             :start-authorized-listening new-identity})

          ; I just now logged out.
          {:db (assoc (:db cofx) :my-identity nil)
           :stop-authorized-listening old-identity})))))


(rf/reg-fx
  :start-authorized-listening
  (fn [{:keys [private-ref presence-ref]}]
    (fb/on-change "value" private-ref :private)
    (fb/note-presence-changes presence-ref)))


(rf/reg-fx
  :stop-authorized-listening
  (fn [{:keys [private-ref presence-ref]}]
    (.off private-ref)
    (.off presence-ref)
    ; TODO: Does .off really work? Try logging out, :online is false -- OK.
    ;       Disconnect from network, then reconnect. :online becomes true. How?
    ;       We're still logged out, so shouldn't know which user's :online to set.
    (fb/reset-into-ref
      presence-ref
      {:online             false
       :online-change-time fb/timestamp-placeholder})))


(rf/reg-event-fx
  :log-out
  (fn [cofx _]
    {:db                (app/note-pending cofx :log-out)
     :firebase-sign-out fb/firebase-auth-object}))


(reg-setter :private [:my-identity :private])

