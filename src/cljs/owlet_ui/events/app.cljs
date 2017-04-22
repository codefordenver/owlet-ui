(ns owlet-ui.events.app
  (:require [clojure.string :as clj-str]
            [re-frame.core :as rf]
            [camel-snake-kebab.core :refer [->kebab-case]]
            [owlet-ui.config :as config]
            [owlet-ui.rf-util :refer [reg-setter]]
            [owlet-ui.db :as db]
            [owlet-ui.helpers :refer [keywordize-name parse-platform]]))


(defn note-pending
  "Records a \"pending\" message (e.g. a keyword) in the :my-identity map,
  indicating to the GUI that the indicated process has started but not yet
  completed.
  "
  [cofx msg]
  (assoc-in (:db cofx) [:my-identity :pending] msg))


(reg-setter :set-sidebar-state [:app :open-sidebar])


(reg-setter :show-bg-img-upload [:showing-bg-img-upload])


(rf/reg-event-db
  :initialize-db
  (fn [_ _]
    db/default-db))


(rf/reg-event-db
  :set-active-view
  (fn [db [_ active-view]]
    (let [search (aget (js->clj (js/document.getElementsByClassName "form-control")) 0)]
      (when-not (nil? search)
        (set! (.-value search) "")
        (.blur search))
      (-> db
          (assoc :active-view active-view)
          (assoc-in [:app :open-sidebar] false)))))


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
                  :search-results-view    (-> db
                                              :activities-by-branch-in-view
                                              :display-name)}
          default-title (:welcome-view titles)
          document-title (or (titles active-view) (clj-str/capitalize (or val "")))
          title-template (str document-title " | " config/project-name)
          title (or title-template default-title)]
      (assoc-in db [:app :title] title))))


(rf/reg-event-fx
  :update-user-background!
  (fn [{{{my-db-ref :firebase-db-ref} :my-identity} :db} [_ url]]
    {:firebase-reset-into-ref [my-db-ref {:background-image-url url}]
     :dispatch                [:show-bg-img-upload false]}))


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

    (rf/dispatch [:set-active-view :search-results-view])
    (rf/dispatch [:set-active-document-title! term])

    ;; by branch
    ;; ---------

    (let [search-term (keywordize-name term)
          activities (:activities db)]

      (if-let [filtered-set (search-term (:activities-by-branch db))]
        (do
          (set! (.-location js/window) (str "/#/" (->kebab-case term)))
          (assoc db :activities-by-branch-in-view filtered-set))

        ;; by skill
        ;; --------

        (let [filtered-set (filterv #(when (contains? (:skill-set %) search-term) %) activities)]
          (if (seq filtered-set)
            (do
              (set! (.-location js/window) (str "/#/search/" (->kebab-case term)))
              (assoc db :activities-by-branch-in-view (hash-map :activities filtered-set
                                                                :display-name term)))

            ;; by activity name (title)
            ;; ------------------------

            (let [filtered-set (filterv #(when (= (get-in % [:fields :title]) term) %) activities)]
              (if (seq filtered-set)
                (let [activity (first filtered-set)
                      activity-id (get-in activity [:sys :id])]
                  (set! (.-location js/window) (str "/#/activity/#!" activity-id))
                  (assoc db :activity-in-view activity))

                ;; by platform
                ;; -----------

                (let [filtered-set (filterv #(let [platform (-> (get-in % [:fields :platform])
                                                                parse-platform)]
                                               (when (= platform term) %)) activities)]
                  (if (seq filtered-set)
                    (do
                      (set! (.-location js/window) (str "/#/search/" (->kebab-case term)))
                      (assoc db :activities-by-branch-in-view (hash-map :activities filtered-set
                                                                        :display-name term)))
                    (assoc db :activities-by-branch-in-view "none")))))))))))
