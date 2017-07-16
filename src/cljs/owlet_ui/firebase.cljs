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


(defn then-dispatch
  "Given a JS Promise or firebase.Promise object, returns it or, if the second
  argument is not empty, returns a new Promise that will dispatch the re-frame
  event when the given Promise has completed, i.e., it calls something like
  (dispatch [:an-event-id rslt-map ...]).

  Here, :an-event-id is the given value of completed-event-id, rslt-map will
  have value {:ok-value v} or {:error-reason r}, and \"...\" are extra event
  args to be dispatched, if any. The value v in rslt-map {:ok-value v} will
  be the result from the given promise when it is fulfilled. If instead the
  promise rejected, the dispatched event will have rslt-map {:error-reason r},
  where r is an instance of firebase.FirebaseError, indicating the reason for
  rejection provided by the promise.

  See https://firebase.google.com/docs/reference/js/firebase.Promise
  and https://firebase.google.com/docs/reference/js/firebase.FirebaseError
  "
  [promise [completed-event-id & event-args]]
  (if completed-event-id
    (letfn [(callback [flag]
              (fn [value-or-reason]
                (-> [completed-event-id {flag value-or-reason}]
                  (into event-args)  ; Appends event vector with args.
                  rf/dispatch)))]
      (.then promise (callback :ok-value) (callback :error-reason)))
    promise))


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


(defn vec->path-str
  "Given a sequence of keywords, strings, or symbols (e.g. a path vector for
  get-in), returns a string suitable for use as a Firebase path argument.
  "
  [v]
  (->> v (map name) (interleave (repeat "/")) rest (apply str)))


(defn path-str->db-ref
  "Returns a Firebase ref for the node at the given path string relative to
  firebase-db-ref, or to the first argument if called with two args.
  "
  ([rel-path] (path-str->db-ref firebase-db-ref rel-path))
  ([db-ref rel-path] (.child db-ref rel-path)))


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
  ; A list must be provided to this effect function, as for function sign-in,
  ; above. E.g., your reg-event-fx registered function could return
  ; {:firebase-sign-in  [fb/firebase-auth-object a-token :some-event]}
  (partial apply sign-in))


(rf/reg-fx
  :firebase-sign-out
  ; The desired firebase.auth.Auth instance must be provided for this effect.
  ; E.g., your reg-event-fx registered function could return
  ; {:firebase-sign-out fb/firebase-auth-object}.
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
                       (fn [fb-user-obj]
                         (rf/dispatch
                           (apply vector event-id fb-user-obj args)))))


;  ;  ;  ;  ;  ;  ;  ;  Communicating with Firebase DB  ;  ;  ;  ;  ;  ;  ;  ;


(def legal-db-value?
  "Returns true iff the given Clojure value can be stored as a value in the
  Firebase database, say using reset-ref or change-on. Most Clojure values could
  be \"converted\" to JSON just using clj->js, but important meaning could be
  lost. So, to enforce explicitness, we require that only \"stringish\" values
  (strings, symbols, or keywords) be used as keys in the case of a map
  argument. The other argument values resulting in a true result are numbers,
  the timestamp-placeholder, and maps or sequential collections with legal
  values as defined here.
  "
  (let [stringish? (some-fn string? symbol? keyword?)]
    (comp boolean (some-fn nil?
                           boolean?
                           number?
                           stringish?
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


(defn- apply-setter-to-ref
  [fb-setter db-ref v & event-id-args]
  {:pre [(or (legal-db-value? v)
             (.log js/console (str) "Illegal Firebase value: " (pr-str v)))
         db-ref]}
  (-> db-ref
    (fb-setter (clj->js v))
    (then-dispatch event-id-args)))


(def reset-ref
  (partial apply-setter-to-ref (memfn set v)))


(rf/reg-fx
  :firebase-reset-ref

  ; [db-ref v & event-id-args]
  ;
  ; Asynchronously assigns the given clojure value at the given Firebase
  ; database reference. If an event vector is given as the third argument,
  ; it will be dispatched upon completion.
  ;
  ; Note that the Clojure value v must pass precondition legal-db-value?. If
  ; it is nil, the location corresponding to the ref will be deleted. The same
  ; applies to empty collections as values, like [], {}, or #{}. (The Firebase
  ; database does not store null values or empty collections.)
  ;
  ; Returns a js/Promise, which will perform (or has performed) the db-ref.set
  ; method call.

  (partial apply reset-ref))


(def reset-into-ref
  (partial apply-setter-to-ref (memfn update v)))

(rf/reg-fx
  :firebase-reset-into-ref

  ; [db-ref v & event-id-args]
  ;
  ; Asynchronously merges the key/values of the given associative Clojure value
  ; v (say a map or vector) into the given Firebase database reference. Any
  ; values at keys not present in the Clojure value will not be changed. If an
  ; event vector is given as the third argument, it will be dispatched upon
  ; completion.
  ;
  ; Note that the Clojure value v must pass precondition legal-db-value?. If it
  ; is nil, the location corresponding to the ref will be deleted. The same
  ; applies to empty collections as values, like [], {}, or #{}. (The Firebase
  ; database does not store null values or empty collections.)
  ;
  ; Returns a js/Promise, which will perform (or has performed) the
  ; db-ref.update method call.)

  (partial apply reset-into-ref))


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

  The first argument must be one of the strings \"value\", \"child_added\",
  \"child_changed\", \"child_removed\", or \"child_moved\", as in
  firebase.database.Reference's on() method. Use \"value\", for instance, if
  you want to capture changes to a single node in the database, or
  \"child_changed\" to watch for changes to individual children of the node. See
  https://firebase.google.com/docs/reference/js/firebase.database.Reference#on

  Returns the callback function passed to the ref's .on method. It can be used
  to turn off observation as follows:
  (.off \"value\" db-ref the-returned-function). See
  https://firebase.google.com/docs/reference/js/firebase.database.Reference#off
  "
  [event-type db-ref event-id & args]
  (.on db-ref
       event-type

       (fn [snapshot]
         (let [snap-as-clj (-> snapshot .val (js->clj :keywordize-keys true))]
           (rf/dispatch (apply vector event-id snap-as-clj args))))

       (fn [error]
         (js/console.log
           (str "owlet-ui.firebase/on-change "
                "calling firebase.database.Reference's .on():\n"
                error)))))


(defn note-presence-changes
  "Tracks the client's connection to Firebase by registering a function to
  listen for any change in node /.info/connected in the given database ref.
  If called with just one argument, this function updates the given ref with a
  boolean at key \"online\" and the number of milliseconds since the epoch at
  key \"online-change-time\". If called with three arguments, the second arg.
  must be a map whose keys and values will be used to update the given db-ref
  when a connection is established. The update will use the data in the third
  argument will when the connection is lost.

  Note that not all the contents of the ref are replaced; values for only the
  keys \"online\" and \"online-change-time\" are written. Similarly, if called
  with three arguments, only the values for keys in the given maps are written.

  Note also that, when disconnected, the \"online-change-time\" integer
  recorded locally must be the time known by the LOCAL system. The value
  recorded on the Firebase server, however, will be the SERVER'S time.
  Once the connection is reestablished, however, the server's value will be
  recorded locally.

  Returns a function listening to node \".info/connected\". You can use it with
  the firebase.database.Reference method off() to tell the server to stop
  listening for presence changes.
  "
  ([db-ref]
   (note-presence-changes
     db-ref
     {:online true,  :online-change-time timestamp-placeholder}
     {:online false, :online-change-time timestamp-placeholder}))

  ([db-ref online-vals offline-vals]
   (letfn [(update-presence-info [target-ref vals]
             ; Note that target-ref may be just db-ref (a
             ; firebase.database.Reference object), OR its associated
             ; OnDisconnect object. Notice that timestamp-placeholder works
             ; locally too!
             (.update target-ref
                      (clj->js vals)
                      #(when %
                         (.log js/console
                               (str "owlet-ui.firebase/note-presence-changes \n"
                                    "calling firebase.database.OnDisconnect's"
                                    ".update():\n"
                                    (.toString %))))))]

     (.on (-> db-ref .-root (.child ".info/connected"))
          "value"

          (fn [snapshot]
            (if (.val snapshot)
              (do
                ; We're now online. Set the current state AND prepare the future
                ; setting, for when the server detects the next disconnection.
                (update-presence-info db-ref online-vals)
                (update-presence-info (.onDisconnect db-ref) offline-vals))
                ; This new firebase.database.OnDisconnect object will be used
                ; at most once.

              ; Otherwise, we're currently offline, so just record the state
              ; locally.
              (update-presence-info db-ref offline-vals)))

          ; Log any error:
          #(js/console.log (str "owlet-ui.firebase/on-presence-change \n"
                                "calling firebase.database.Reference's .on():\n"
                                (.toString %)))))))


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
        sending-reaction (reaction (reset-ref db-ref @sub-reaction))
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
  (.once (path-str->db-ref rel-path)
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

  (if js-file
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
               (conj (when-not error default-error))
               (conj (when complete-with-snapshot snap-complete))
               clj->js)))

    (let [err-msg "Please select a file to be uploaded."]
      (if error
        (error (js/Error. err-msg))
        (js/console.log "upload-file:" err-msg)))))


(defn ez-upload-file
  "Provides a straightforward way for a GUI to upload a local file to Firebase
  Storage and receive the URL pointing to the new file there.

  When called, ez-upload-file initiates the upload of the file selected in an
  <input type=\"file\" id=input-elem-id> element in the current document,
  where input-elem-id is the string given in the first argument. The given
  destination directory string must be relative to the root of Firebase
  Storage. As the upload proceeds, atom progress-pct-atom is periodically
  updated with a number: the percentage of uploaded bytes. Atom error-atom will
  contain the nil value unless the user did not provide a file in the input
  element or if there was some other problem preventing the upload. In this
  case error-atom will contain an error message.

  Finally, when the upload is complete, a re-frame event is dispatched, which
  is a vector of the given event-id, the new URL string, and any provided args.
  "
  [input-elem-id dest-dir progress-pct-atom error-atom event-id & args]

  (when progress-pct-atom (reset! progress-pct-atom 0))
  (when error-atom        (reset! error-atom        nil))
  (apply
    upload-file

    ; JS File object, or nil if none selected:
    (-> input-elem-id js/document.getElementById (aget "files" 0))

    ; Upload options:
    (concat
      [:into-dir               dest-dir
       :complete-with-snapshot (fn [snapshot]
                                 (->> snapshot          ; The firebase.storage.UploadTaskSnapshot
                                      .-downloadURL     ; The new URL of the stored file.
                                      (conj args)       ; Makes (url arg1 arg2 ...)
                                      (into [event-id]) ; Makes [event-id url arg1 arg2 ...)
                                      rf/dispatch))]
      (and progress-pct-atom
           [:next (fn [task-snapshot]
                    ; What to do after each batch of bytes has been transfered.
                    ; Argument is a firebase.storage.UploadTaskSnapshot. See
                    ; https://firebase.google.com/docs/reference/js/firebase.storage.UploadTaskSnapshot
                    (let [total      (.-totalBytes task-snapshot)
                          transfered (.-bytesTransferred task-snapshot)]
                      (reset! progress-pct-atom
                              (js/Math.round (* 100 (/ transfered total))))))])
      (and error-atom
           [:error (fn [error]
                     (reset! error-atom (.-message error)))]))))


(defn delete-file-at-url
  "Returns a Promise containing void, which will attempt to delete the file
  in Firebase Storage at the given URL.
  "
  [url]
  (-> url storage-ref-for-url .delete))


(rf/reg-fx
  :delete-file-at-url

  ; Called with either a URL string or a vector containing the URL, an event
  ; id, and any event arguments. Returns a Promise containing void, which will
  ; attempt to delete the file at the given URL. The resulting promise resolves
  ; if the deletion succeeded and rejects if it failed, including if the file
  ; referred-to by the URL didn't exist.
  ;
  ; If you provide an event id as the second member of the vector argument,
  ; then the handler for that event will be dispatched-to after the attempt to
  ; delete succeeds or fails. The second member of the dispatched event vector
  ; will be a \"rslt-map\", as in function then-dispatch. Remaining members in
  ; the given vector argument will become the remaining members of the
  ; dispatched event vector.

  (fn [url-etc]
    (let [[url & event-id-args] (if (string? url-etc) [url-etc] url-etc)]
      (-> url
        delete-file-at-url        ; Returns a promise.
        (then-dispatch event-id-args)))))

