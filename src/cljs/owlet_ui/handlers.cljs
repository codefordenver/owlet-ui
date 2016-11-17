(ns owlet-ui.handlers
  (:require [re-frame.core :as re]
            [owlet-ui.config :as config]
            [owlet-ui.auth0 :as auth0]
            [owlet-ui.firebase :as fb]
            [ajax.core :refer [GET POST PUT]]
            [camel-snake-kebab.core :refer [->camelCase]]))


(defn add-url-safe-name-to-activities-collection
  "assocs :url-safe-name into activities collection"
  [activities]
  (for [activity activities
        :let [pluck-name (get-in activity [:fields :title])]]
       (assoc activity :url-safe-name (->camelCase pluck-name))))


(defn register-setter-handler
  "Provides an easy way to register a new handler returning a map that differs
  from the given one only at the location at the given path vector. Simply
  provide the event-key keyword and the db-path vector. Optionally, the new
  value may be the result of a function you provide a that takes as arguments
  the new value and any values following the new value in the vector given to
  the handler. For example, if we called

    (register-setter-handler :my-handler
                             [:path :in :app-db]
                             (fn [new-val up?]
                               (if up? (upper-case new-val) new-val)))

    (dispatch [:my-handler \"new words\" true])

  Now, evaluating

    (get-in @app-db [:path :in :app-db])

  will result in \"NEW WORDS\".
  "

  ([event-key db-path]
   (register-setter-handler event-key db-path identity))

  ([event-key db-path f]
   (re/register-handler
     event-key
     (fn [db [_ new-data & args]]
       (assoc-in db db-path (apply f new-data args))))))


(defn my
  "Applies the given \"-in\" function on the current user's data in the
  given db map. So if db looks like

    {... :my-user-id :xxx, :users {:yyy {}, ...}

  then (my assoc-in db [:k1 :k2] \"new value\") will return the value

    {... :my-user-id :xxx, :users {:yyy {},
                                   :xxx {:k1 {:k2 \"new value\"}}
                                   ...}

  Of course, you can similarly call with get-in or update-in.
  "
  [f-in db ks & args]
  (let [path (concat [:users (:my-user-id db)] ks)]
    (apply f-in db path args)))


(re/register-handler
  :initialize-db
  (fn [_ _]
    config/default-db))


(re/register-handler
  :set-active-view
  (fn [db [_ active-view route-parameter]]
    (when (or (= :track-activities-view active-view) (= :activity-view active-view))
      (re/dispatch [:get-library-content route-parameter]))
    (assoc db :active-view active-view)))


(re/register-handler
  :authenticated
  (fn [db [_ {token :id_token}]]
    (fb/sign-in fb/firebase-auth-object token :firebase-auth-error)
    db))


(re/register-handler
  :firebase-auth-error
  (fn [db [_ error]]
    (prn error)       ; TODO: Put up login-error GUI component.
    db))


(re/register-handler
  :firebase-user
  (fn [db [_ user]]
    (if user
      (do
        (js/console.log "Firebase user is signed in. uid:" (.-uid user))
        (assoc db :my-user-id (.-uid user)))
      (do
        (js/console.log "Firebase user is signed out.")
        (dissoc db :my-user-id)))))


(re/register-handler
  :user-has-logged-in-out!
  (re/path [:user])
  (fn [db [_ val]]
    ;; reset user-bg-image on logout
    (when (false? val)
      (do
        (re/dispatch [:reset-user-bg-image! config/default-header-bg-image])
        (re/dispatch [:reset-user-db!])))
    (assoc db :logged-in? val)))


(re/register-handler
  :reset-user-db!
  (re/path [:user])
  (fn [_ [_ _]]
    config/default-user-db))


(re/register-handler
  :update-sid-and-get-cms-entries-for
  (re/path [:user])
  (fn [db [_ sid]]
    (GET (str config/server-url "/api/content/entries?social-id=" sid)
         {:response-format :json
          :keywords?       true
          :handler         #(re/dispatch [:process-fetch-entries-success! %1])})
    (assoc db :social-id sid)))


(re/register-handler
  :process-fetch-entries-success!
  (re/path [:user :content-entries])
  (fn [db [_ entries]]
    (re/dispatch [:set-user-background-image! entries])
    (conj db entries)))


(re/register-handler
  :set-user-background-image!
  (re/path [:user :background-image])
  (fn [_ [_ coll]]
    (let [filter-user-bg-image (fn [c]
                                 (filterv #(= (get-in % [:sys :contentType :sys :id])
                                              "userBgImage") c))
          user-bg-image-entries (last (filter-user-bg-image coll))
          entry-id (get-in user-bg-image-entries [:sys :id])]
      ;; set :background-image-entry-id
      (re/dispatch [:set-backround-image-entry-id! entry-id])
      (get-in user-bg-image-entries [:fields :url :en-US]))))


(re/register-handler
  :set-backround-image-entry-id!
  (re/path [:user :background-image-entry-id])
  (fn [_ [_ id]]
    id))


(re/register-handler
  :update-user-background!
  (fn [db [_ url]]
    ;; if we have a url and an entry-id, aka existing entry for *userBgImage*
    ;; perform an update
    (let [entry-id (get-in db [:user :background-image-entry-id])]
      (if (and url entry-id)
        (PUT
          (str config/server-url "/api/content/entries")
          {:response-format :json
           :keywords?       true
           :params          {:content-type "userBgImage"
                             :fields       {:url      {"en-US" url}
                                            :socialid {"en-US" (get-in db [:user :social-id])}}
                             :entry-id     entry-id}
           :handler         #(re/dispatch [:update-user-background-after-successful-post! %1])
           :error-handler   #(prn %)})
        (POST
          (str config/server-url "/api/content/entries")
          {:response-format :json
           :keywords?       true
           :params          {:content-type  "userBgImage"
                             :fields        {:url      {"en-US" url}
                                             :socialid {"en-US" (get-in db [:user :social-id])}}
                             :auto-publish? true}
           :handler         #(re/dispatch [:update-user-background-after-successful-post! %1])
           :error-handler   #(prn %)})))
    db))


(re/register-handler
  :update-user-background-after-successful-post!
  (re/path [:user :background-image])
  (fn [_ [_ res]]
    (re/dispatch [:set-backround-image-entry-id! (get-in res [:sys :id])])
    (get-in res [:fields :url :en-US])))


(re/register-handler
  :reset-user-bg-image!
  (re/path [:user :background-image])
  (fn [_ [_ url]]
    url))


(re/register-handler
  :get-auth0-profile
  (fn [db [_ _]]
    (when-let [user-token (.getItem js/localStorage "userToken")]
      (.getProfile
        auth0/lock
        user-token
        (fn [err profile]
          (if (some? err)
            ;; delete expired token
            (when user-token
              (.removeItem js/localStorage "userToken"))
            (let [user-id (.-user_id profile)]
              (re/dispatch [:user-has-logged-in-out! true])
              (re/dispatch [:update-sid-and-get-cms-entries-for user-id])
              (fb/on-presence-change
                (fb/db-ref-for-path (str "users/" user-id))
                :user-presence-changed)
              (register-setter-handler
                :user-presence-changed
                [:users (keyword user-id)]))))))
    db))

(re/register-handler
  :get-library-content
  (fn [db [_ route-params]]
    (when (empty? (:activity-models db))
      (re/dispatch [:get-activity-models]))
    (GET (str config/server-url "/api/content/entries?library-view=true&space-id=" config/library-space-id)
         {:response-format :json
          :keywords?       true
          :handler         #(re/dispatch [:activities-get-successful % route-params])
          :error-handler   #(prn %)})
    db))

(re/register-handler
  :activities-get-successful
  (fn [db [_ res route-params]]

    ; Obtains the URL for each preview image, and adds a :url field next to
    ; its :id field in [:activities :fields :preview :sys] map.

    (let [url-for-id                                        ; Maps preview image IDs to associated URLs.
          (->> (get-in res [:includes :Asset])
               (map (juxt (comp :id :sys)
                          (comp :url :file :fields)))
               (into {}))
          _db_ (assoc db                                    ; Return new db, adding :url field to its [... :sys] map.
                 :activities
                 (for [item (:items res)]
                   (update-in item
                              [:fields :preview :sys]
                              (fn [{id :id :as sys}]
                                (assoc sys :url (url-for-id id))))))]
      (when route-params
        ;; i.e. when we are navigating to /tracks/:track/:activity
        (let [{:keys [track activities activity]} route-params]
          (re/dispatch [:set-activities-by-track-in-view :track-id track])
          ;; i.e. when we request data for single activity view
          (if activity
            (re/dispatch [:activities-by-track (:activities _db_) track activity])
            (re/dispatch [:activities-by-track (:activities _db_) track]))
          (when activities
            (re/dispatch [:set-activities-in-view activities]))))
      _db_)))

(re/register-handler
  :set-activities-by-track-in-view
  (fn [db [_ prop arg]]
    (case prop
      :display-name (assoc-in db [:activities-by-track-in-view :display-name] arg)
      :track-id (assoc-in db [:activities-by-track-in-view :track-id] (keyword arg)))))

(re/register-handler
  :set-activities-in-view
  (re/path [:activities-in-view])
  (fn [_ [_ activity-id]]
    activity-id))

(re/register-handler
  :activities-by-track
  (fn [db [_ activities track-id activity]]
    (let [filtered-activities (filterv #(= (get-in % [:sys :contentType :sys :id]) track-id) activities)
          processed-activities (add-url-safe-name-to-activities-collection filtered-activities)]
      (when activity
        (re/dispatch [:set-activity-in-view processed-activities activity]))
      (assoc-in db [:activities-by-track (keyword track-id)] processed-activities))))

(re/register-handler
  :get-activity-models
  (fn [db [_ _]]
    (re/dispatch [:set-loading-state! true])
    (GET (str config/server-url "/api/content/models/" config/library-space-id)
         {:response-format :json
          :keywords?       true
          :handler         #(re/dispatch [:get-activity-models-successful %])
          :error-handler   #(prn %)})
    db))

(re/register-handler
  :get-activity-models-successful
  (fn [db [_ res]]
      (re/dispatch [:set-loading-state! false])
      (re/dispatch [:set-track-display-name (:models (:models res))])
      (assoc db :activity-models (:models res))))

(re/register-handler
  :set-track-display-name
  (fn [db [_ models]]
    (let [track-id (get-in db [:activities-by-track-in-view :track-id])
          display-name (:name
                        (first
                         (filter #(if (= track-id (keyword (:model-id %))) %) models)))]
      (assoc-in db
            [:activities-by-track-in-view :display-name] display-name))))

(re/register-handler
  :set-activity-in-view
    (re/path [:activity-in-view])
  (fn [_ [_ activities activity]]
    (some #(when (= (:url-safe-name %) activity) %)
          activities)))

(re/register-handler
  :set-loading-state!
  (re/path [:app :loading?])
  (fn [_ [_ state]]
      state))
