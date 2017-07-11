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
  :get-content-from-contentful
  (fn [{db :db} route-args]
    (let [[_ route-dispatch route-param] route-args]
      (if-not (seq (:activities db))
        {:http-xhrio {:method          :get
                      :uri             space-endpoint
                      :response-format (ajax/json-response-format {:keywords? true})
                      :on-success      [:get-content-from-contentful-success route-args]}}
        {:dispatch [route-dispatch route-param]}))))

(rf/reg-event-fx
  :get-content-from-contentful-success
  (fn [{db :db} [_ route-args {activities :activities metadata :metadata platforms :platforms}]]
    (let [route-dispatch (second route-args)
          route-param (get route-args 2)
          branches (:branches metadata)
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
      {:db (assoc db
            :activity-platforms (map #(:fields %) platforms)
            :activities activities
            :activity-branches branches
            :skills skills
            :activities-by-branch activities-by-branch
            :activity-titles activity-titles)
       :dispatch [route-dispatch route-param]})))


; route dispatches

(rf/reg-event-fx
  :show-branches
  (fn [_ _]
    {:dispatch-n (list [:set-active-view :branches-view]
                       [:set-active-document-title! "Branches"])}))

(rf/reg-event-fx
  :show-branch
  (fn [_ [_ route-param]]
    {:dispatch-n (list [:set-active-view :filtered-activities-view "branch"]
                       [:filter-activities-by-search-term route-param]
                       [:set-active-document-title! route-param])}))

(rf/reg-event-fx
  :show-platform
  (fn [_ [_ route-param]]
    {:dispatch-n (list [:set-active-view :filtered-activities-view "platform"]
                       [:filter-activities-by-search-term route-param]
                       [:set-active-document-title! route-param])}))

(rf/reg-event-fx
  :show-skill
  (fn [_ [_ route-param]]
    {:dispatch-n (list [:set-active-view :filtered-activities-view "branch"]
                       [:filter-activities-by-search-term route-param]
                       [:set-active-document-title! route-param])}))

(rf/reg-event-fx
  :show-activity
  (fn [_ [_ route-param]]
    {:dispatch-n (list [:set-active-view :activity-view]
                       [:set-activity-in-view route-param])}))


(rf/reg-event-db
  :filter-activities-by-search-term
  (fn [db [_ term]]
    (let [search-term (keywordize-name term)
          activities (:activities db)
          set-path (fn [path]
                    (set! (.-location js/window) (str "/#/" path)))]

      ;; by branch
      ;; ---------

      (if-let [filtered-set (search-term (:activities-by-branch db))]
        (do
          (set-path (str "branch/" (->kebab-case term)))
          (assoc db :activities-by-filter filtered-set))

        ;; by skill
        ;; --------

        (let [filtered-set (filter #(when (contains? (:skill-set %) term) %) activities)]
          (if (seq filtered-set)
            (do
              (set-path (str "skill/" (->kebab-case term)))
              (assoc db :activities-by-filter (hash-map :activities filtered-set
                                                        :display-name term)))

            ;; by activity name (title)
            ;; ------------------------

            (let [filtered-set (filter #(when (= (get-in % [:fields :title]) term) %) activities)]
              (if (seq filtered-set)
                (let [activity (first filtered-set)
                      activity-id (get-in activity [:sys :id])]
                  (set-path (str "activity/#!" activity-id))
                  (assoc db :activity-in-view activity))

                ;; by activity id
                ;; ------------------------

                (if-let [activity (some #(when (= (get-in % [:sys :id]) term) %) activities)]
                  (assoc db :activity-in-view activity)

                  ;; by platform
                  ;; -----------

                  (let [filtered-set (filter #(let [platform (get-in % [:fields :platform :search-name])]
                                                 (when (= platform term) %)) activities)
                        platform-name (get-in (first filtered-set) [:fields :platform :name])]
                    (if (seq filtered-set)
                      (let [description (some #(when (= platform-name (:name %)) (:description %))
                                          (:activity-platforms db))]
                        (set-path (str "platform/" term))
                        (assoc db :activities-by-filter (hash-map :activities filtered-set
                                                                  :display-name platform-name
                                                                  :description description)))
                      (assoc db :activities-by-filter "error"))))))))))))
