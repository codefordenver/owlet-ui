(ns owlet-ui.events.app
  (:require [clojure.string :as clj-str]
            [re-frame.core :as rf]
            [camel-snake-kebab.core :refer [->kebab-case]]
            [owlet-ui.config :as config]
            [owlet-ui.rf-util :refer [reg-setter]]
            [owlet-ui.db :as db]
            [owlet-ui.helpers :refer [keywordize-name]]))


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
  (fn [db [_ active-view opts]]
    (let [email (:email opts)
          search (aget (js->clj (js/document.getElementsByClassName "form-control")) 0)]
      (when-not (nil? search)
        (set! (.-value search) "")
        (.blur search))
      (-> db
          (assoc :active-view active-view)
          (assoc-in [:app :route-opts] email)
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
                  :filtered-activities-view (-> db
                                                :activities-by-filter
                                                :display-name)}
          default-title (:welcome-view titles)
          document-title (or (titles active-view) (clj-str/capitalize (or val "")))
          title-template (str document-title " | " config/project-name)
          title (or title-template default-title)]
      (assoc-in db [:app :title] title))))


(rf/reg-event-fx
  :update-user-background!
  (fn [{{{my-db-ref :private-ref} :my-identity} :db} [_ url]]
    {:firebase-reset-into-ref [my-db-ref {:background-image-url url}]
     :dispatch                [:show-bg-img-upload false]}))


(rf/reg-event-db
  :set-activity-in-view
  (fn [db [_ activity-id all-activities]]
    (when-let [activity-match (some #(when (= (get-in % [:sys :id]) activity-id) %)
                                  (or (:activities db) all-activities))]
      (assoc db :activity-in-view activity-match))))
