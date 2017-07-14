(ns owlet.events.contentful
  (:require [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax :refer [GET POST PUT]]
            [owlet.db :as db]
            [owlet.config :as config]
            [owlet.rf-util :refer [reg-setter]]
            [camel-snake-kebab.core :refer [->kebab-case]]
            [owlet.helpers :refer
             [keywordize-name remove-nil]]))


(defonce space-endpoint
         (str config/server-url
              "/content/space?library-view=true&space-id="
              config/owlet-activities-3-space-id))


(rf/reg-event-fx
  :get-library-content-from-contentful
  (fn [{db :db} [_ route-params]]
    {:db         (merge (assoc-in db [:app :loading?] true)
                        (assoc-in db [:app :route-params] route-params))
     :http-xhrio {:method          :get
                  :uri             space-endpoint
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [:get-library-content-from-contentful-successful]}}))


(rf/reg-event-db
  :get-library-content-from-contentful-successful
  (fn [db [_ {activities :activities metadata :metadata platforms :platforms}]]

    (rf/dispatch [:process-activity-metadata metadata])
    (-> db
      (assoc
        :activity-platforms (map #(:fields %) platforms)
        :activities activities))))

(rf/reg-event-db
  :process-activity-metadata
  (fn [db [_ metadata]]
    (let [branches (:branches metadata)
          skills (:skills metadata)
          all-activities (:activities db)
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
        (let [{:keys [activity branch skill platform]} route-params]
          (cond platform (rf/dispatch [:filter-activities-by-search-term platform])
                skill    (rf/dispatch [:filter-activities-by-search-term skill])
                activity (rf/dispatch [:set-activity-in-view activity all-activities])
                branch   (let [activities-by-branch-in-view ((keyword branch) activities-by-branch)]
                          (rf/dispatch [:set-activities-by-branch-in-view branch activities-by-branch-in-view])))))
      (assoc db :activity-branches branches
                :skills skills
                :activities-by-branch activities-by-branch
                :activity-titles activity-titles))))
