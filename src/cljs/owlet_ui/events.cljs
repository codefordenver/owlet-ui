(ns owlet-ui.events
  (:require [clojure.string :as clj-str]
            [re-frame.core :as re]
            [owlet-ui.db :as db]
            [owlet-ui.config :as config]
            [owlet-ui.firebase :as fb]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax :refer [GET POST PUT]]
            [camel-snake-kebab.core :refer [->kebab-case]]))


(defonce library-content-url
         (str config/server-url
              "/api/content/entries?library-view=true&space-id="
              config/owlet-activities-2-space-id))


(defonce get-metadata-url
         (str config/server-url
              "/api/content/metadata/"
              config/owlet-activities-2-space-id))


(re/reg-cofx
  :set-loading!
  (fn [cofx val]
    (assoc-in cofx [:db :app :loading?] val)))


(re/reg-cofx
  :close-sidebar!
  (fn [cofx]
    (let [db (:db cofx)]
      (when-not (= (db :active-view) :welcome-view)
        (js/closeSidebar))
      (assoc-in cofx [:db :app :open-sidebar] true))))


(re/reg-cofx
  :navigate-to-view!
  (fn [cofx new-view]
    (assoc-in cofx [:db :active-view] new-view)))


(re/reg-event-db
  :set-active-document-title!
  (fn [db [_ val]]
    (let [active-view (:active-view db)
          titles {:welcome-view           "Welcome"
                  :branches-view          "Branches"
                  :activity-view          (-> db
                                              :activity-in-view
                                              :fields
                                              :title)
                  :branch-activities-view (-> (:activities-by-branch-in-view db)
                                              :display-name)}
          default-title (:welcome-view titles)
          document-title (or (titles active-view) (clj-str/capitalize (or val "")))
          title-template (str document-title " | " config/project-name)
          title (or title-template default-title)]
      (assoc-in db [:app :title] title))))

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
   (re/reg-event-db
     event-key
     (fn [db [_ new-data & args]]
       (assoc-in db db-path (apply f new-data args))))))


(re/reg-event-db
  :initialize-db
  (fn [_ _]
    db/default-db))


(re/reg-event-db
  :set-active-view
  [(re/inject-cofx :close-sidebar!)]
  (fn [db [_ active-view]]
    (assoc db :active-view active-view)))


(re/reg-event-db
  :user-has-logged-in-out!
  (re/path [:user])
  (fn [db [_ val]]
    ;; reset user-bg-image on logout
    (when (false? val)
      (do
        (re/dispatch [:reset-user-bg-image! config/default-header-bg-image])
        (re/dispatch [:reset-user-db!])))
    (assoc db :logged-in? val)))


(re/reg-event-db
  :reset-user-db!
  (re/path [:user])
  (fn [_ [_ _]]
    db/default-user-db))


(re/reg-event-db
  :update-sid-and-get-cms-entries-for
  (re/path [:user])
  (fn [db [_ sid]]
    (GET (str config/server-url "/api/content/entries?social-id=" sid)
         {:response-format :json
          :keywords?       true
          :handler         #(re/dispatch [:process-fetch-entries-success! %1])})
    (assoc db :social-id sid)))


(re/reg-event-db
  :process-fetch-entries-success!
  (re/path [:user :content-entries])
  (fn [db [_ entries]]
    (re/dispatch [:set-user-background-image! entries])
    (conj db entries)))


(re/reg-event-db
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


(re/reg-event-db
  :set-backround-image-entry-id!
  (re/path [:user :background-image-entry-id])
  (fn [_ [_ id]]
    id))


(re/reg-event-db
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


(re/reg-event-db
  :update-user-background-after-successful-post!
  (re/path [:user :background-image])
  (fn [_ [_ res]]
    (re/dispatch [:set-backround-image-entry-id! (get-in res [:sys :id])])
    (get-in res [:fields :url :en-US])))


(re/reg-event-db
  :reset-user-bg-image!
  (re/path [:user :background-image])
  (fn [_ [_ url]]
    url))


(re/reg-event-db
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


(re/reg-event-fx
  :get-library-content-from-contentful
  (fn [{db :db} [_ route-params]]
    ;; short-circuit xhr request when we have activity data
    (when-not (seq (:activities db))
      {:db         (merge (assoc-in db [:app :loading?] true)
                          (assoc-in db [:app :route-params] route-params))
       :http-xhrio {:method          :get
                    :uri             library-content-url
                    :response-format (ajax/json-response-format {:keywords? true})
                    :on-success      [:get-library-content-from-contentful-successful]}})))


(re/reg-event-db
  :get-library-content-from-contentful-successful
  (fn [db [_ res]]
    (re/dispatch [:get-activity-metadata])
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
                       (for [item (:items res)
                             :let [activity (update-in item [:fields :preview :sys]
                                                       (fn [{id :id :as sys}]
                                                         (assoc sys :url (url-for-id id))))
                                   image-gallery (get-in activity [:fields :imageGallery])
                                   image-gallery-ids (map #(-> % :sys :id) image-gallery)
                                   image-gallery-urls (map #(url-for-id %) image-gallery-ids)]]
                         (update-in activity [:fields] #(assoc % :image-gallery-urls image-gallery-urls)))))]
      _db_)))


(re/reg-event-db
  :get-activity-metadata
  (fn [db _]
    (GET get-metadata-url
         {:response-format :json
          :keywords?       true
          :handler         #(re/dispatch [:get-activity-metadata-successful %])
          :error-handler   #(prn %)})
    db))

(re/reg-event-db
  :get-activity-metadata-successful
  [(re/inject-cofx :set-loading! false)]
  (fn [db [_ res]]
    (let [branches (:branches res)
          skills (:skills res)
          all-activities (:activities db)

          branches-template (->> (mapv (fn [branch]
                                         (hash-map (keyword (->kebab-case branch))
                                                   {:activities   []
                                                    :display-name branch
                                                    :count        0
                                                    :preview-urls  []})) branches)
                                 (into {}))

          activities-by-branch (->> (mapv (fn [branch]
                                            (let [[branch-key branch-vals] branch]
                                              (let [display-name (:display-name branch-vals)
                                                    matches (filterv (fn [activity]
                                                                       (some #(= display-name %)
                                                                             (get-in activity [:fields :branch])))
                                                                     all-activities)
                                                    preview-urls (mapv #(get-in % [:fields :preview :sys :url]) matches)]
                                                (if (seq matches)
                                                  (hash-map branch-key
                                                            {:activities   matches
                                                             :display-name display-name
                                                             :count        (count matches)
                                                             :preview-urls  preview-urls})
                                                  branch))))
                                          branches-template)
                                    (into {}))]
      (when-let [route-params (get-in db [:app :route-params])]
        (let [{:keys [activity branch]} route-params]
          (when activity
            (re/dispatch [:set-activity-in-view activity all-activities]))
          (when branch
            (let [activities-by-branch-in-view ((keyword branch) activities-by-branch)]
              (re/dispatch [:set-activities-by-branch-in-view branch activities-by-branch-in-view])
              (assoc db :activity-branches branches
                        :skills skills
                        :activities-by-branch activities-by-branch)))))
      (assoc db :activity-branches branches
                :skills skills
                :activities-by-branch activities-by-branch))))


(re/reg-event-db
  :set-activities-by-branch-in-view
  (fn [db [_ branch-name activities-by-branch]]
    (let [activities-by-branch ((keyword branch-name) (or (:activities-by-branch db) activities-by-branch))]
      (assoc db :activities-by-branch-in-view activities-by-branch))))


(re/reg-event-db
  :set-activity-in-view
  (fn [db [_ activity-id all-activities]]
    (assoc db :activity-in-view (some #(when (= (get-in % [:sys :id]) activity-id) %)
                                      (or (:activities db) all-activities)))))


(re/reg-event-db
  :filter-activities-by-search-term
  [(re/inject-cofx :navigate-to-view! :branch-activities-view)]
  (fn [db [_ search-term]]
    ;; by branch
    (let [lookup (-> (->kebab-case search-term)
                     keyword)]
      (if-let [filtered-set (lookup (:activities-by-branch db))]
        (assoc db :activities-by-branch-in-view filtered-set)
        db))))
    ;; by skill ,,,
