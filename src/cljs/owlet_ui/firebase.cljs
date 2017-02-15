(ns owlet-ui.firebase
  ;           (                           )
  ;           )\ )   (    (       (    ( /(      )          (
  ;          (()/(   )\   )(     ))\   )\())  ( /(   (     ))\
  ;           /(_)) ((_) (()\   /((_) ((_)\   )(_))  )\   /((_)
  ;          (_) _|  (_)  ((_) (_))   | |(_) ((_)_  ((_) (_))
  ;           |  _|  | | | '_| / -_)  | '_ \ / _` | (_-< / -_)
  ;           |_|    |_| |_|   \___|  |_.__/ \__,_| /__/ \___|
  ;
  "Utilities for working with the Firebase backend-as-a-service platform.
  See https://firebase.google.com
  "
  (:require [owlet-ui.config :refer [firebase-app-init]]
            [owlet-ui.async :refer [repeatedly-running]]
            [reagent.ratom :refer-macros [reaction]]
            [re-frame.core :as rf]

            ; Adds <script src="https://www.gstatic.com/firebasejs/3.4.0/firebase.js"></script>
            ; to index.html . This is required for def. of js/firebase, etc.
            [cljsjs.firebase]))


(defonce firebase-app
  ; The global firebase.app.App instance for use by this application.
  ; There must be only one with a particular name.
  ; See https://firebase.google.com/docs/reference/js/firebase.app.App.html .
  (.initializeApp js/firebase
                  (clj->js firebase-app-init)         ; Options.
                  "owlet-ui.firebase/firebase-app"))  ; Just a name.


(def timestamp-placeholder
  "A value you can provide that the firebase server will automatically replace
  with the date/time when the assignment took place there.
  "
  (-> js/firebase .-database .-ServerValue .-TIMESTAMP))


;  ;  ;  ;  ;  ;  ;  ;  ;   Defining Firebase refs   ;  ;  ;  ;  ;  ;  ;  ;  ;


(def firebase-auth-object
  "The firebase.auth.Auth instance to authenticate users of this firebase-app.
  "
  (.auth firebase-app))


(def firebase-db-ref
  "A Firebase ref (a firebase.database.Reference instance) referring to the
  root of the database of this firebase-app.
  "
  (-> firebase-app .database .ref))


(defn db-ref-for-path
  "Returns a Firebase ref for the node at the given path string relative to
  firebase-db-ref.
  "
  [rel-path]
  (.child firebase-db-ref rel-path))


(def firebase-storage-ref
  "A Firebase ref (a firebase.storage.Reference instance) referring to the
  \"bucket\" root of the storage for this firebase-app.
  "
  (-> firebase-app .storage .ref))


(defn storage-ref-for-url
  "Returns a Firebase ref (a firebase.storage.Reference instance) referring to
  the storage in this firebase-app at the given URL.
  "
  [url]
  (-> firebase-app .storage (.refFromURL url)))


;  ;  ;  ;  ;  ;  ;  ;  ;   Firebase authorization   ;  ;  ;  ;  ;  ;  ;  ;  ;


(defn sign-in
  "Signs in to the given firebase.auth.Auth instance using the given custom
  token, e.g. a Google or Facebook token provided by Auth0 at login. If the
  token is invalid or expired, a re-frame event is dispatched with first
  element the given event id (typically a keyword), second element the
  resulting firebase.auth.Error object as a ClojureScript map with keyword keys,
  and the remaining elements the given arguments, if any. For error keys and
  codes, see
  https://firebase.google.com/docs/reference/js/firebase.auth.Auth#signInWithCustomToken
  and
  https://firebase.google.com/docs/reference/js/firebase.auth.Error
  Returns a firebase.Promise containing the signed-in Firebase user object.
  To obtain the user object, it is preferable to define a handler and use
  function on-auth-change to set up an event that triggers it.
  "
  [auth-obj fb-token fail-event-id & fail-args]
  (-> auth-obj
      (.signInWithCustomToken fb-token)
      (.catch (fn [fb-error]
                (rf/dispatch
                  (apply vector fail-event-id (js->clj fb-error) fail-args))))))


(rf/reg-fx
  :firebase-sign-in
  ; A list must be provided for this effect, which contains just the args. for
  ; function sign-in, above.
  (partial apply sign-in))


(rf/reg-fx
  :firebase-sign-out
  ; The desired firebase.auth.Auth instance must be provided for this effect,
  ; such as firebase-auth-object, above.
  (memfn signOut))


(defn on-auth-change
  "Registers a re-frame event to be fired whenever the state of the given
  firebase.auth.Auth instance changes. The event is a vector whose first
  element is the given event id (typically a keyword), followed by the
  currently signed-in user as a ClojureScript map with keyword keys (or nil if
  no longer signed in), followed by the given arguments, if any. Returns a
  no-arg. function, which may be called to stop listening for these changes.
  "
  [auth-obj event-id & args]
  (.onAuthStateChanged auth-obj
                       (fn [fb-user]
                         (rf/dispatch
                           (apply vector event-id (js->clj fb-user) args)))))


;  ;  ;  ;  ;  ;  ;  ;  Communicating with Firebase DB  ;  ;  ;  ;  ;  ;  ;  ;


(def legal-db-value?
  "Returns true iff the given Clojure value can be stored as a value in the
  Firebase database, say using set-ref or change-on. Most Clojure values could
  be \"converted\" to JSON just using clj->js, but important meaning could be
  lost. So, to enforce explicitness, we require that only \"stringish\" values
  (strings, symbols, or keywords) be used as keys in the case of a map
  argument. The other argument values resulting in a true result are numbers,
  the timestamp-placeholder, and maps or sequential collections with legal
  values as defined here.
  "
  (let [stringish? (some-fn string? symbol? keyword?)]
    (comp boolean (some-fn number?
                           stringish?
                           nil?
                           (partial = timestamp-placeholder)
                           (every-pred
                             (some-fn sequential? set?)
                             not-empty
                             #(every? legal-db-value? %))
                           (every-pred
                             map?
                             not-empty
                             (comp (partial every? stringish?) keys)
                             #(every? legal-db-value? (vals %)))))))


(defn set-ref
  "Asynchronously assigns the given clojure value v at Firebase reference
  a-ref. If a no-arg callback function is given as third argument, it is
  called upon completion.

  Note that v must pass precondition legal-db-value?, and if v is nil, the
  location corresponding to a-ref will be deleted. The same applies to empty
  collections as values, like [], {}, or #{}. (The Firebase databse does not
  store null values or empty collections.)

  Returns a js/Promise, which will perform (or has performed) the .set method
  call.
  "
  ([a-ref v]
   (set-ref a-ref v #(do)))

  ([a-ref v callback]
   {:pre [(or (legal-db-value? v)
              (.log js/console (str "Illegal Firebase value:" (pr-str v))))
          (fn? callback)]}
   (let [value (clj->js v)]
     (.set a-ref value callback))))


(defn on-change
  "This is the preferred way to keep your re-frame app continuously updated
  to changes occurring in the Firebase node indicated by the given ref.
  Argument db-ref is the ref of the node to be observed. When a change happens
  there, a re-frame event will be dispatched. It will be a vector whose first
  element is event-id, second element is the new data from the Firebase node,
  and remaining elements are the remaining arguments. Note that the event will
  fire right away with initial data.

  The Firebase database is part of the outside world, like the end user is.
  To receive data from the user, we set up components to receive data from the
  user and dispatch it to handlers we specify. So just think of this function
  as a way to set up a sort of VIRTUAL component to receive data from a
  Firebase database ref and dispatch to a handler we specify.

  Returns the callback function passed to the ref's .on method. It can be used
  to turn off observation as follows:
  (.off \"value\" db-ref the-returned-function). See
  https://firebase.google.com/docs/reference/js/firebase.database.Reference#off
  "
  [db-ref event-id & args]
  (.on db-ref
       "value"
       (fn [snapshot]
         (let [snap-as-clj (-> snapshot .val (js->clj :keywordize-keys true))]
           (rf/dispatch (apply vector event-id snap-as-clj args))))
       #(.log js/console
              (str "owlet-ui.firebase/on-change"
                   "calling firebase.database.Reference's .on():\n"
                   (.toString %)))))


(defn note-presence-changes
  "Tracks the client's connection to Firebase by registering a function to
  listen for any change in node /.info/connected in the given database ref.
  The function updates the given ref with a boolean at key \"online\" and the
  number of milliseconds since the epoch at key \"online-change-time\". Note
  that the contents of the ref are not replaced; values for only these keys
  are written.

  Note also that, when disconnected, the \"online-change-time\" integer
  recorded locally must be the time known by the LOCAL system. The value
  recorded on the Firebase server, however, will be the SERVER'S time.
  Once the connection is reestablished, however, the server's value will be
  recorded locally, and an event will be dispatched as usual.

  Returns a vector containing two functions. The first is the function
  listening to node /.info/connected, and the second is listening to db-ref,
  as in on-change. You can use these with the firebase.database.Reference
  method off(). See the on-change doc.
  "
  [db-ref]
  (letfn [(update-presence-info [db-obj connected?]
            ; Note that db-obj may be just db-ref (a firebase.database.Reference
            ; object), OR its associated firebase.database.OnDisconnect object.
            ; Notice that timestamp-placeholder works locally too!
            (.update db-obj
                     (clj->js {:online             connected?
                               :online-change-time timestamp-placeholder})
                     #(when %
                        (.log js/console
                              (str "owlet-ui.firebase/on-presence-change \n"
                                   "calling firebase.database.OnDisconnect's"
                                   ".uppdate():\n"
                                   (.toString %))))))]

    [(.on (db-ref-for-path ".info/connected")
          "value"

          (fn [snapshot]
            ; Update db-ref with connection status, incl. time connected.
            (update-presence-info db-ref (.val snapshot))
            (when (.val snapshot)
              ; Tell db-ref to do update on the server only if connection is
              ; lost. This happens at most once.
              (update-presence-info (.onDisconnect db-ref) false)))

          ; Log any error:
          #(.log js/console (str "owlet-ui.firebase/on-presence-change \n"
                                 "calling firebase.database.Reference's .on():\n"
                                 (.toString %))))]))


(defn change-on
  "This is the preferred way to keep the Firebase node indicated by a given ref
  continuously updated to changes in your re-frame app. Argument db-ref is the
  ref of the node to be updated. It is essentially \"subscribed\" to the
  registered subscription defined by a vector of the subscription-args. The
  resulting reaction will be polled as if it were dereferrenced in the hiccup
  code of a component definition. Whenever it changes, its new value will be
  uploaded to the Firebase node. Note that the upload happens immediately with
  initial data.

  Important: For this function to work, you need to make sure that function
  owlet-ui.async/repeatedly-run has been called with no arguments and is
  running. This is the polling mechanism.

  This function is the inverse of on-change, sending data to the outside world.
  To send data to the user, we set up components that receive data from
  subscriptions we specify and display it to the user. So just think of this
  function as a way to set up a sort of VIRTUAL component to receive data from
  a subscription we specify and send it to a Firebase database ref.

  Returns the no-arg function that is polled: It does the actual upload when
  the subscription notices a change (and returns the firebase.Promise resulting
  from ref's .set method). To halt the polling of the-returned-fn, execute the
  following:

      (swap! owlet-ui.async/repeatedly-running disj the-returned-fn)
  "
  [db-ref & subscription-args]
  (let [sub-reaction     (rf/subscribe (vec subscription-args))
        sending-reaction (reaction (set-ref db-ref @sub-reaction))
        repeated-fn      (fn [] @sending-reaction)]
    (swap! repeatedly-running conj repeated-fn)
    repeated-fn))


(defn promise-for-path
  "Returns a promise to run the given callback function with the value in the
  database at the given path relative to firebase-db-ref. The value given to
  the callback is a ClojureScript value, such as a map, seq, string, or number.
  This is handy for ad-hoc queries at the REPL.
  "
  [rel-path callback]
  (.once (db-ref-for-path rel-path)
         "value"
         #(-> % .val js->clj callback)
         #(.log js/console
            (str "owlet-ui.firebase/promise-for-path \n"
                 "calling firebase.database.Reference's .once():\n"
                 (.toString %)))))


;  ;  ;  ;  ;  ;  ;  Communicating with Firebase Storage   ;  ;  ;  ;  ;  ;  ;


(defn upload-file
  "Given a JavaScript File or Blob instance, uploads its data asynchronously to
  firebase-storage-ref. The destination will be the root directory unless you
  provide an :into-dir value, a directory pathname without a trailing '/'. The
  other options, if provided, must be functions of one argument. If no :error
  function is provided, a function logging a message to the console is used. If
  a :complete-with-snapshot function is provided, it will be called upon
  completion of the upload with the UploadTaskSnapshot provided by the
  UploadTask. See
  https://firebase.google.com/docs/reference/js/firebase.storage.UploadTaskSnapshot
  A 0-arg \"unsubscribe\" function is returned from upload-file, which will
  remove all (next, error, or complete) callbacks from the running task, if
  called.
  "
  [js-file & {:keys [into-dir error complete-with-snapshot] :as options}]

  (let [dir           (or into-dir "")
        path          (str dir "/" (.-name js-file))
        ref           (.child firebase-storage-ref path)
        task          (.put ref js-file)
        default-error ; Key-value pair for default "error" function.
                      [:error
                       (fn [js-error]
                         (.log js/console
                               "upload-file:"
                               (str "Could not upload file '"
                                    (.-name js-file)
                                    "'.")
                               js-error))]
        snap-complete ; Key-value pair for the "complete" function expected by
                      ; firebase.storage.UploadTask's .on method, which is a
                      ; 0-arg function. So we wrap the given
                      ; :complete-with-snapshot 1-arg function, passing it the
                      ; new file URL.
                      [:complete
                       #(complete-with-snapshot (.-snapshot task))]]

    ; Execute the file-upload task, substituting :error or :complete functions,
    ; if necessary.
    (.on task
         js/firebase.storage.TaskEvent.STATE_CHANGED
         (-> options
             (dissoc :into-dir :complete-with-snapshot)   ; Keys not expected by .on.
             (conj (when (not error) default-error))
             (conj (when complete-with-snapshot snap-complete))
             clj->js))))


(defn delete-file-at-url
  "Returns a Promise containing void, which deletes the file at the given URL.
  The promise resolves if the deletion succeeded and rejects if it failed,
  including if the file refered-to by the URL didn't exist.
  "
  [url]
  (-> url storage-ref-for-url .delete))
