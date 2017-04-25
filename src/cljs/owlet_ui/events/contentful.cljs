(ns owlet-ui.events.contentful
  (:require [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax :refer [GET POST PUT]]
            [owlet-ui.db :as db]
            [owlet-ui.config :as config]
            [owlet-ui.rf-util :refer [reg-setter]]
            [camel-snake-kebab.core :refer [->kebab-case]]
            [owlet-ui.helpers :refer
             [keywordize-name remove-nil parse-platform]]))

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

    ; Obtains the URL, width, and height for each image (asset), and
    ;  - for each *preview* image, adds a :url field next to its :id field in
    ;    [:activities :fields :preview :sys] map.
    ;  - for each *gallery* image, adds a map to :image-gallery-items field in
    ;    [:activities :fields] map.

    (let [assets
          (get-in entries [:includes :Asset])

          image-by-id     ; Maps image IDs to associated URL, width, and height.
          (->> assets
            (map
              (juxt
                (comp :id :sys)
                #(hash-map
                   :url (get-in % [:fields :file :url])
                   :w   (get-in % [:fields :file :details :image :width])
                   :h   (get-in % [:fields :file :details :image :height]))))
            (into {}))]

      (-> db
        (assoc
          :activities
          (into []
            (for [item (:items entries)
                  :let [image-gallery (get-in item [:fields :imageGallery])
                        image-gallery-items (->> image-gallery               ; Gallery list.
                                                 (map (comp :sys :id))     ; Gall. img. ids.
                                                 (map image-by-id))]]        ; Their images.
              (-> item
                (update-in [:fields :preview :sys]       ; Add img. URL at
                           (fn [{id :id :as sys}]        ;  [.. :sys :url]
                             (assoc sys
                               :url
                               (get-in image-by-id [id :url]))))
                (assoc-in [:fields :image-gallery-items] ; Add gallery imgs.
                          image-gallery-items)))))

        (update
          :activities                                    ; Add :skills-set
          (partial mapv (fn [activity]
                          (or (some->> activity
                                :fields
                                :skills
                                remove-nil
                                seq           ; some->> gives nil if empty
                                (map keywordize-name)
                                set
                                (assoc activity :skill-set))
                              activity))))))))


(rf/reg-event-db
  :process-activity-metadata
  (fn [db [_ metadata]]
    (let [branches (:branches metadata)
          skills (:skills metadata)
          all-activities (:activities db)
          platforms (remove-nil (map #(get-in % [:fields :platform ]) all-activities))
          platforms-normalized (->> platforms (map parse-platform))
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
                        :activity-platforms platforms-normalized)))
          (when search
            (rf/dispatch [:filter-activities-by-search-term search]))))
      (assoc db :activity-branches branches
                :skills skills
                :activities-by-branch activities-by-branch
                :activity-titles activity-titles
                :activity-platforms platforms-normalized))))
