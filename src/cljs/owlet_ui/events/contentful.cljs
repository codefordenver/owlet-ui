(ns owlet-ui.events.contentful
  (:require [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax :refer [GET POST PUT]]
            [owlet-ui.db :as db]
            [owlet-ui.config :as config]
            [owlet-ui.rf-util :refer [reg-setter]]
            [camel-snake-kebab.core :refer [->kebab-case]]
            [owlet-ui.helpers :refer
             [keywordize-name remove-nil]]))


(defonce space-endpoint
         (str config/server-url
              "/owlet/api/content/space?library-view=true&space-id="
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
    (let [branches (:branches metadata)
          skills (:skills metadata)
          activity-titles (remove-nil (map #(get-in % [:fields :title]) activities))
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
                                                              activities)
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
      (-> db
        (assoc
          :activity-platforms (map #(:fields %) platforms)
          :activities activities
          :activity-branches branches
          :skills skills
          :activities-by-branch activities-by-branch
          :activity-titles activity-titles)))))
