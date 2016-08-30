(ns owlet-ui.firebase
  (:require [owlet-ui.config :refer [firebase-app-init]]
            [reagent.ratom :refer-macros [reaction]]
            [re-frame.core :as re]
            [cljsjs.firebase]))


(defonce firebase-app
  (.initializeApp js/firebase (clj->js firebase-app-init)))


(defonce firebase-storage-ref
  (-> firebase-app .storage .ref))


(defonce firebase-db-ref
  (-> firebase-app .database .ref))


(defn ref-for-path
  "Returns a Firebase ref for the node at the given path string relative to
  firebase-db-ref.
  "
  [rel-path]
  (.child firebase-db-ref rel-path))


(defn storage-ref-for-url
  [url]
  (-> firebase-app .storage (.refFromURL url)))


(defn upload-at-ref
  [map-or-list a-ref]
  (.set a-ref (clj->js map-or-list)))


(defn link-with-atom!
  [db-ref an-atom]

  ; Side effect! Assign event callback to db-ref:
  ; First clean out previous callback definitions during development. See
  ; http://grokbase.com/t/gg/firebase-talk/14bnqq68g0/firebase-do-i-need-to-call-off-when-a-reference-is-removed
  (.off db-ref)
  ; Define callback that just copies all data to the given atom.
  (.on db-ref "value" (fn [snapshot]
                        (let [snap-as-clj (-> snapshot
                                              .val
                                              (js->clj :keywordize-keys true))]
                          (reset! an-atom snap-as-clj))))

  ; For convenience, return a 0-arg function to upload the contents of an-atom.
  #(upload-at-ref @an-atom db-ref))


(defn on-change
  [db-ref event-id & args]
  (.on db-ref
       "value"
       (fn [snapshot]
         (let [snap-as-clj (-> snapshot .val (js->clj :keywordize-keys true))]
           (re/dispatch (apply vector event-id snap-as-clj args))))))


(defn change-on
  [db-ref & subscription-args]
  (let [react (re/subscribe (vec subscription-args))]
    (reaction (.set db-ref (clj->js @react)))))


(defn promise-for-path
  "Returns a promise to run the given callback function with the value in the
  database at the given path relative to firebase-db-ref. The value given to
  the callback is a ClojureScript value, such as a map, seq, string, or number.
  This is handy for ad-hoc queries at the REPL.
  "
  [rel-path callback]
  (.once (ref-for-path rel-path)
         "value"
         #(-> % .val js->clj callback)))


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
  [js-file & {:keys [into-dir next error complete-with-snapshot] :as options}]

  (.log js/console
    "todomvc.firebase/upload-file:"
    (str "Uploading '" (.-name js-file) "' into directory '" into-dir "'"))

  (let [dir           (or into-dir "")
        path          (str dir "/" (.-name js-file))
        ref           (.child firebase-storage-ref path)
        task          (.put ref js-file)
        default-error ; Key-value pair for default "error" function.
                      [:error
                       (fn [js-error]
                         (.log js/console
                               "todomvc.firebase/upload-file:"
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


(defn delete-url
  "Returns a Promise containing void, which deletes the file at the given URL.
  The promise resolves if the deletion succeeded and rejects if it failed,
  including if the file refered-to by the URL didn't exist.
  "
  [url]
  (-> url storage-ref-for-url .delete))


(def timestamp-placeholder
  (-> js/firebase .-database .-ServerValue .-TIMESTAMP))
