(ns owlet-ui.handlers
  (:require [re-frame.core :as re]
            [owlet-ui.db :as db]
            [owlet-ui.config :as config]
            [owlet-ui.firebase :as fb]
            [ajax.core :refer [GET POST PUT]]
            [camel-snake-kebab.core :refer [->kebab-case]]))

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


(re/register-handler
  :initialize-db
  (fn [_ _]
    db/default-db))


(re/register-handler
  :set-active-view
  (fn [db [_ active-view route-parameter]]
    (prn (str "active-view: " active-view ", route-parameter: " route-parameter))
    (when (or (= :branch-activities-view active-view) (= :activity-view active-view))
      (re/dispatch [:get-library-content route-parameter]))
    (assoc db :active-view active-view)))


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
    db/default-user-db))


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
    (when-let [user-token (.getItem js/localStorage "owlet:user-token")]
      (.getProfile
        config/lock
        user-token
        (fn [err profile]
          (if (some? err)
            ;; delete expired token
            (when user-token
              (.removeItem js/localStorage "owlet:user-token"))
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
    (when (empty? (:activity-branches db))
      (re/dispatch [:get-activity-branches]))
    (GET (str config/server-url "/api/content/entries?library-view=true&space-id=" config/owlet-activities-2-space-id)
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
                 (into []
                   (for [item (:items res)]
                     (update-in item
                                [:fields :preview :sys]
                                (fn [{id :id :as sys}]
                                  (assoc sys :url (url-for-id id)))))))]

      (when route-params
        ;; i.e. when we are navigating to /tracks/:track/:activity
        (let [{:keys [activities activity]} route-params]
          ;; i.e. when we request data for single activity view
          (when activities
            (re/dispatch [:set-activities-in-view activities]))))
      _db_)))

(re/register-handler
  :set-activities-by-branch-in-view
  (fn [db [_ display-name]]
    (assoc-in db [:activities-by-branch-in-view] display-name)))

(re/register-handler
  :set-activities-in-view
  (re/path [:activities-in-view])
  (fn [_ [_ activity-id]]
    activity-id))

(re/register-handler
  :activities-by-track
  (fn [db [_ activities track-id activity]]
    (let [filtered-activities (filterv #(= (get-in % [:sys :contentType :sys :id]) track-id) activities)]
          ; processed-activities (add-url-safe-name-to-activities-collection filtered-activities)]
      (when activity
        (re/dispatch [:set-activity-in-view filtered-activities activity]))
      (assoc-in db [:activities-by-track (keyword track-id)] filtered-activities))))

(re/register-handler
  :get-activity-branches
  (fn [db [_ _]]
    (re/dispatch [:set-loading-state! true])
    (GET (str config/server-url "/api/content/branches/" config/owlet-activities-2-space-id)
         {:response-format :json
          :keywords?       true
          :handler         #(re/dispatch [:get-activity-branches-successful %])
          :error-handler   #(prn %)})
    db))

(re/register-handler
  :get-activity-branches-successful
  (fn [db [_ res]]
    (re/dispatch [:set-loading-state! false])
    (let [branches (:branches (:branches res))
          all-activities (:activities db)

          branches-template (into {} (mapv (fn [branch]
                                             (hash-map (keyword (->kebab-case branch))
                                               {:activities []
                                                :display-name branch})) branches))

          activities-by-branch (into {} (mapv (fn [[branch-key branch-val]]
                                                (let [display-name (:display-name branch-val)
                                                      activities   (:activities branch-val)
                                                      matches (filterv (fn [a]
                                                                         (some #(= display-name %)
                                                                               (get-in a [:fields :branch])))
                                                                     all-activities)]
                                                  (if (seq matches)
                                                    (hash-map branch-key
                                                              {:activities matches
                                                               :display-name display-name})
                                                    (hash-map branch-key
                                                              {:activities activities
                                                               :display-name display-name}))))
                                              branches-template))]

      ;(re/dispatch [:set-branch-display-name branches])
      (assoc db :activity-branches (:branches res)
                :activities-by-branch activities-by-branch))))

(re/register-handler
 :set-branch-display-name
 (fn [db [_ branches]]
   (let [branch-name (get-in db [:activities-by-track-in-view :branch-name])
         display-name (:name
                       (first
                        (filter #(if (= branch-name (keyword (:model-id %))) %) branches)))]
     (assoc-in db
           [:activities-by-track-in-view :display-name] display-name))))

(re/register-handler
  :set-activity-in-view
    (re/path [:activity-in-view])
  (fn [_ [_ activities activity-id]]
    (some #(when (= (get-in % [:sys :id]) activity-id) %)
          activities)))

(re/register-handler
  :set-loading-state!
  (re/path [:app :loading?])
  (fn [_ [_ state]]
      state))
