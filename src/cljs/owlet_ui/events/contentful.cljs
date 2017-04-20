(ns owlet-ui.events.contenful
  (:require [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax :refer [GET POST PUT]]
            [owlet-ui.db :as db]
            [owlet-ui.config :as config]
            [owlet-ui.events.helpers :refer [reg-setter]]
            [camel-snake-kebab.core :refer [->kebab-case]]
            [owlet-ui.helpers :refer
             [keywordize-name remove-nil parse-platform clean-search-term]]))

(defonce space-endpoint
         (str config/server-url
              "/api/content/space?library-view=true&space-id="
              config/owlet-activities-2-space-id))

(rf/reg-event-fx
  :get-library-content-from-contentful
  (fn [{db :db} [_ route-params]]
    ;; short-circuit xhr request when we have activity data
    (when-not (seq (:activities db))
      {:db         (merge (assoc-in db [:app :loading?] true)
                          (assoc-in db [:app :route-params] route-params))
       :http-xhrio {:method          :get
                    :uri             space-endpoint
                    :response-format (ajax/json-response-format {:keywords? true})
                    :on-success      [:get-library-content-from-contentful-successful]}})))


(rf/reg-event-db
  :get-library-content-from-contentful-successful
  (fn [db [_ {entries :entries metadata :metadata}]]

    (rf/dispatch [:process-activity-metadata metadata])

    ; Obtains the URL for each preview image, and adds a :url field next to
    ; its :id field in [:activities :fields :preview :sys] map.

    (let [url-for-id                                        ; Maps preview image IDs to associated URLs.
          (->> (get-in entries [:includes :Asset])
               (map (juxt (comp :id :sys)
                          (comp :url :file :fields)))
               (into {}))
          _db_ (assoc db                                    ; Return new db, adding :url field to its [... :sys] map.
                 :activities
                 (into []
                       (for [item (:items entries)
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
  :process-activity-metadata
  (fn [db [_ metadata]]
    (let [branches (:branches metadata)
          skills (:skills metadata)
          all-activities (:activities db)
          platforms (remove-nil (map #(get-in % [:fields :platform]) all-activities))
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
