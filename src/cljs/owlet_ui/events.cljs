(ns owlet-ui.events
  (:require [clojure.string :as clj-str]
            [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax :refer [GET POST PUT]]
            [owlet-ui.db :as db]
            [owlet-ui.config :as config]
            [owlet-ui.firebase :as fb]
            [camel-snake-kebab.core :refer [->kebab-case]]
            [owlet-ui.helpers :refer
             [keywordize-name remove-nil parse-platform clean-search-term]]))


(defonce library-content-url
         (str config/server-url
              "/api/content/entries?library-view=true&space-id="
              config/owlet-activities-2-space-id))


(defonce get-metadata-url
         (str config/server-url
              "/api/content/metadata/"
              config/owlet-activities-2-space-id))


(rf/reg-cofx
  :set-loading!
  (fn [cofx val]
    (assoc-in cofx [:db :app :loading?] val)))


(rf/reg-cofx
  :close-sidebar!
  (fn [cofx]
    (let [db (:db cofx)]
      (when-not (= (db :active-view) :welcome-view)
        (assoc-in cofx [:db :app :open-sidebar] false)))))


(rf/reg-event-db
  :set-active-document-title!
  (fn [db [_ val]]
    (let [active-view (:active-view db)
          titles {:welcome-view           "Welcome"
                  :branches-view          "Branches"
                  :activity-view          (-> db
                                              :activity-in-view
                                              :fields
                                              :title)
                  :branch-activities-view (-> db
                                              :activities-by-branch-in-view
                                              :display-name)
                  :search-results-view (-> db
                                           :activities-by-branch-in-view
                                           :display-name)}
          default-title (:welcome-view titles)
          document-title (or (titles active-view) (clj-str/capitalize (or val "")))
          title-template (str document-title " | " config/project-name)
          title (or title-template default-title)]
      (assoc-in db [:app :title] title))))


(defn reg-setter
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
   (reg-setter event-key db-path identity))

  ([event-key db-path f]
   (rf/reg-event-db
     event-key
     (fn [db [_ new-data & args]]
       (assoc-in db db-path (apply f new-data args))))))


(defn- note-pending
  "Records a \"pending\" message (e.g. a keyword) in the :my-identity map,
  indicating to the GUI that the indicated process has started but not yet
  completed.
  "
  [cofx msg]
  (assoc-in (:db cofx) [:my-identity :pending] msg))


(rf/reg-event-db
  :initialize-db
  (fn [_ _]
    db/default-db))


(rf/reg-event-db
  :set-active-view
  [(rf/inject-cofx :close-sidebar!)]
  (fn [db [_ active-view]]
    (let [search (aget (js->clj (js/document.getElementsByClassName "form-control")) 0)]
      (when-not (nil? search)
        (set! (.-value search) "")
        (.blur search))
      (assoc db :active-view active-view))))


(reg-setter :show-bg-img-upload [:showing-bg-img-upload])


(rf/reg-event-fx
  :update-user-background!
  (fn [{{{my-db-ref :firebase-db-ref} :my-identity} :db} [_ url]]
    {:firebase-reset-into-ref [my-db-ref {:background-image-url url}]
     :dispatch                [:show-bg-img-upload false]}))


(rf/reg-event-fx
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


(rf/reg-event-db
  :get-library-content-from-contentful-successful
  (fn [db [_ res]]
    (rf/dispatch [:get-activity-metadata])
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
                         (update-in activity [:fields] #(assoc % :image-gallery-urls image-gallery-urls)))))

          __db__ (update _db_
                         :activities                        ; Return new db, adding :skills-set
                         #(mapv (fn [activity]
                                  (let [skills (remove-nil (-> activity :fields :skills))]
                                    (if (seq skills)
                                      (assoc activity :skill-set (set (map keywordize-name skills)))
                                      activity))) %))]

      __db__)))


(rf/reg-event-db
  :get-activity-metadata
  (fn [db _]
    (GET get-metadata-url
         {:response-format :json
          :keywords?       true
          :handler         #(rf/dispatch [:get-activity-metadata-successful %])
          :error-handler   #(prn %)})
    db))

(rf/reg-event-db
  :get-activity-metadata-successful
  [(rf/inject-cofx :set-loading! false)]
  (fn [db [_ res]]
    (let [branches (:branches res)
          skills (:skills res)
          all-activities (:activities db)
          platforms (remove-nil (map #(get-in % [:fields :platform ]) all-activities))
          platforms-nomalized (->> platforms (map parse-platform))
          activity-titles (remove-nil (map #(get-in % [:fields :title]) all-activities))
          branches-template (->> (mapv (fn [branch]
                                         (hash-map (keywordize-name branch)
                                                   {:activities   []
                                                    :display-name branch
                                                    :count        0
                                                    :preview-urls []})) branches)
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
                                                             :preview-urls preview-urls})
                                                  branch))))
                                          branches-template)
                                    (into {}))]
      (when-let [route-params (get-in db [:app :route-params])]
        (let [{:keys [activity branch search]} route-params]
          (when activity
            (rf/dispatch [:set-activity-in-view activity all-activities]))
          (when branch
            (let [activities-by-branch-in-view ((keyword branch) activities-by-branch)]
              (rf/dispatch [:set-activities-by-branch-in-view branch activities-by-branch-in-view])
              (assoc db :activity-branches branches
                        :skills skills
                        :activities-by-branch activities-by-branch
                        :activity-titles activity-titles
                        :activity-platforms platforms-nomalized)))
          (when search
            (rf/dispatch [:filter-activities-by-search-term search]))))
      (assoc db :activity-branches branches
                :skills skills
                :activities-by-branch activities-by-branch
                :activity-titles activity-titles
                :activity-platforms platforms-nomalized))))


(rf/reg-event-db
  :set-activities-by-branch-in-view
  (fn [db [_ branch-name activities-by-branch]]
    (if-let [activities-by-branch ((keyword branch-name) (or (:activities-by-branch db) activities-by-branch))]
      (assoc db :activities-by-branch-in-view activities-by-branch)
      (assoc db :activities-by-branch-in-view "error"))))

(rf/reg-event-db
  :set-activity-in-view
  (fn [db [_ activity-id all-activities]]
    (if-let [activity-match (some #(when (= (get-in % [:sys :id]) activity-id) %)
                                  (or (:activities db) all-activities))]
      (assoc db :activity-in-view activity-match)
      (assoc db :activity-in-view "error"))))

(rf/reg-event-db
  :filter-activities-by-search-term
  (fn [db [_ term]]

    (set! (.-location js/window) (str "/#/search/" (->kebab-case term)))

    (rf/dispatch [:set-active-document-title! term])

    ;; by branch
    ;; ---------

    (let [search-term (keywordize-name term)
          activities (:activities db)]

      (if-let [filtered-set (search-term (:activities-by-branch db))]
        (assoc db :activities-by-branch-in-view filtered-set)

        ;; by skill
        ;; --------

        (let [filtered-set (filterv #(when (contains? (:skill-set %) search-term) %) activities)]
          (if (seq filtered-set)
            (assoc db :activities-by-branch-in-view (hash-map :activities filtered-set
                                                              :display-name term))

            ;; by activity name (title)
            ;; ------------------------

            (let [filtered-set (filterv #(when (= (get-in % [:fields :title]) term) %) activities)]
              (if (seq filtered-set)
                (assoc db :activities-by-branch-in-view (hash-map :activities filtered-set
                                                                  :display-name term))

                ;; by platform
                ;; -----------

                (let [filtered-set (filterv #(let [platform (-> (get-in % [:fields :platform])
                                                                parse-platform)]
                                               (when (= platform term) %)) activities)]
                  (if (seq filtered-set)
                    (assoc db :activities-by-branch-in-view (hash-map :activities filtered-set
                                                                      :display-name term))
                    (assoc db :activities-by-branch-in-view "none")))))))))))


(rf/reg-event-fx
  :auth0-authenticated
  (fn [cofx [_ {:keys [auth0-token delegation-token]}]]
    {:firebase-sign-in  [fb/firebase-auth-object
                         delegation-token
                         :firebase-sign-in-failed]
     :db                (note-pending cofx :log-in)}))


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
    (let [new-id-kw    (some-> fb-user .-uid keyword)
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


(rf/reg-event-fx
  :log-out
  (fn [cofx _]
    {:db                (note-pending cofx :log-out)
     :firebase-sign-out fb/firebase-auth-object}))


(reg-setter :my-identity [:my-identity])


(reg-setter :firebase-users-change [:users])


(reg-setter :set-sidebar-state [:app :open-sidebar])
