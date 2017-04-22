(ns owlet-ui.events.auth
  (:require [re-frame.core :as rf]
            [owlet-ui.rf-util :refer [reg-setter]]
            [owlet-ui.firebase :as fb]
            [owlet-ui.events.app :as app]))

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


(rf/reg-event-fx
  :firebase-auth-change
  (fn [cofx [_ fb-user]]
    ; If user is logged into firebase, fb-user is a JS object containing
    ; a string in its uid property. Otherwise, fb-user is nil. Thus we will
    ; know whether we're logged-in simply from (:my-user-id db). Also, if
    ; non-nil user-id changed (from nil), then turn on the presence watcher.
    (let [new-id-kw (some-> fb-user .-uid keyword)
          old-identity (-> cofx :db :my-identity)]
      ; Compare user-id with FORMER value at :my-user-id in app-db.
      (if (= new-id-kw (:firebase-id old-identity))
        {}
        {:change-user [new-id-kw old-identity]}))))


(rf/reg-fx
  :change-user
  (fn [[new-id-kw {:keys [firebase-db-ref presence-off-cb]}]]
    (if new-id-kw

      ; User just logged in, so track presence and save the user's firebase id,
      ; the location (ref) in the firebase database where his/her persisted
      ; data is stored, and the callback we'll need to turn off presence when
      ; logging out.
      (let [new-ref (fb/path-str->db-ref (str "users/" (name new-id-kw)))]
        (rf/dispatch
          [:my-identity {:firebase-id     new-id-kw
                         :firebase-db-ref new-ref
                         :presence-off-cb (fb/note-presence-changes new-ref)}]))

      ; Else just logged out. Turn off presence tracking and set :online false.
      ; Also flag that we're logged out with nil for :my-user-id.
      (do (.off firebase-db-ref "value" presence-off-cb)
          ; TODO: Does .off really work? Try logging out, :online is false -- OK.
          ;       Disconnect from network, then reconnect. :online becomes true. How?
          ;       We're still logged out, so shouldn't know which user's :online to set.
          (fb/reset-into-ref
            firebase-db-ref
            {:online             false
             :online-change-time fb/timestamp-placeholder})
          (rf/dispatch [:my-identity nil])))))





