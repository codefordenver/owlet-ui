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
  (fn [{{{:keys [private private-ref]} :my-identity} :db}
       [_ new-url]]
    ; Called after the upload of a background image file was successful, we
    ; update the user's data in the Firebase database, which will automatically
    ; cause app-db to be similarly updated, thanks to the effect
    ; :start-authorized-listening and the handler :private.
    (let [old-url        (:background-image-url private)
          fb-update-data {:background-image-url new-url}]

      {:firebase-reset-into-ref
       [private-ref fb-update-data               ; Where and what to save.
        :user-background-saved new-url old-url]  ; What to do when complete.

       :dispatch
       [:show-bg-img-upload false]})))


(rf/reg-event-fx
  :user-background-saved
  (fn [_ [_ {err :error-reason} new-url old-url]]
    ; Called after an attempt to update the Firebase db with the URL of a new
    ; background image for the user, we delete the old image file if the update
    ; was successful, or we delete the new image file if there was an error.
    (letfn [(url->image-name [url]
              (get (re-find #"%2Fbackground-image%2F([^?]+)" url) 1))
            (same-image-name-in [& urls]
              (->> urls
                (map url->image-name)
                (apply =)))]
      (when (not (same-image-name-in new-url old-url))
        {:delete-file-at-url (if err new-url old-url)}))))
      ; Do nothing if the URL didn't change: return nil.


(rf/reg-event-db
  :set-activity-in-view
  (fn [db [_ activity-id all-activities]]
    (when-let [activity-match (some #(when (= (get-in % [:sys :id]) activity-id) %)
                                  (or (:activities db) all-activities))]
      (assoc db :activity-in-view activity-match))))
