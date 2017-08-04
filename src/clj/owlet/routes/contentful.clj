(ns owlet.routes.contentful
  (:require [compojure.core :refer [defroutes GET POST PUT]]
            [selmer.parser :refer [render-file]]
            [selmer.filters :refer [add-filter!]]
            [ring.util.http-response :refer [ok not-found internal-server-error]]
            [ring.util.response :refer [redirect]]
            [compojure.api.sweet :refer [context]]
            [org.httpkit.client :as http]
            [mailgun.mail :as mail]
            [cheshire.core :as json]
            [camel-snake-kebab.core :refer [->kebab-case]]))

(def creds {:key    (System/getenv "MMM_MAILGUN_API_KEY")
            :domain "mg.codefordenver.org"})

(defonce OWLET-ACTIVITIES-3-MANAGEMENT-AUTH-TOKEN
         (System/getenv "OWLET_ACTIVITIES_3_MANAGEMENT_AUTH_TOKEN"))

(defonce OWLET-ACTIVITIES-3-DELIVERY-AUTH-TOKEN
         (System/getenv "OWLET_ACTIVITIES_3_DELIVERY_AUTH_TOKEN"))

(def owlet-url "http://owlet.codefordenver.org")

(defn epoch [] (int (/ (System/currentTimeMillis) 1000)))

(defonce subscribers-endpoint "https://owlet-users.firebaseio.com/subscribers.json")

(defn subscriber-endpoint [id]
  (str "https://owlet-users.firebaseio.com/subscribers/" id ".json"))

(add-filter! :kebab #(->kebab-case %))

(defn- get-activity-metadata
  "GET all branches in Activity model for owlet-activities-2 space"
  [space-id headers]
  (http/get (format "https://api.contentful.com/spaces/%1s/content_types" space-id) headers))

(defn- get-entry-by-id [space-id entry-id]
  (http/get (format "https://cdn.contentful.com/spaces/%1s/entries/%2s" space-id entry-id)
            {:headers {"Authorization" (str "Bearer " OWLET-ACTIVITIES-3-DELIVERY-AUTH-TOKEN)}}))

(defn- get-asset-by-id [space-id asset-id]
  (http/get (format "https://cdn.contentful.com/spaces/%1s/assets/%2s" space-id asset-id)
            {:headers {"Authorization" (str "Bearer " OWLET-ACTIVITIES-3-DELIVERY-AUTH-TOKEN)}}))

(defn- process-metadata
  [metadata]
  (let [body (json/parse-string metadata true)
        items (body :items)
        activity-model (some #(when (= (:name %) "Activity") %) items)
        activity-model-fields (:fields activity-model)
        pluck-prop (fn [prop]
                     (-> (get-in (some #(when (= (:id %) prop) %) activity-model-fields)
                                 [:items :validations])
                         first
                         :in))
        skills (pluck-prop "skills")
        branches (pluck-prop "branch")]
    {:skills   skills
     :branches branches}))

(defn- filter-entries [content-type items]
  (filter #(= content-type
              (-> % (get-in [:sys :contentType :sys :id])))
          items))

(defn- image-by-id
  "Maps image IDs to associated URL, width, and height."
  [assets]
  (->> assets
       (map
         (juxt
           (comp :id :sys)
           #(hash-map
              :url (get-in % [:fields :file :url])
              :w   (get-in % [:fields :file :details :image :width])
              :h   (get-in % [:fields :file :details :image :height]))))
       (into {})))

(defn- keywordize-name [name]
  (-> name ->kebab-case keyword))

(def remove-nil (partial remove nil?))

(defn- process-activity [activity platforms assets]
  (-> activity
      ; Adds :platform data using :platformRef
      (assoc-in [:fields :platform]
                (some #(when (= (get-in activity [:fields :platformRef :sys :id])
                                (get-in % [:sys :id]))
                         (hash-map :name (get-in % [:fields :name])
                                   :search-name (str (->kebab-case (get-in % [:fields :name])))
                                   :color (get-in % [:fields :color])))
                      platforms))
      ; Adds preview img. URL at [.. :sys :url]
      (update-in [:fields :preview :sys]
                 (fn [{id :id :as sys}]
                   (assoc sys
                     :url
                     (get-in (image-by-id assets) [id :url]))))
      ; Adds :image-gallery-items
      (assoc-in [:fields :image-gallery-items]
                (->> (get-in activity [:fields :imageGallery])
                     (map (comp :id :sys))        ; Gallery image ids.
                     (mapv (image-by-id assets))))
      ; Add :skill-set
      (assoc-in [:fields :skill-set] (or (some->> activity
                                                  :fields
                                                  :skills
                                                  remove-nil
                                                  seq          ; some->> gives nil if empty
                                                  (map keywordize-name)
                                                  set)
                                         activity))))

(defn- process-activities
  [activities platforms assets]
  (for [activity activities]
    (process-activity activity platforms assets)))


(defn handle-get-all-entries-for-given-space

  "asynchronously GET all entries for given space
  optionally pass library-view=true param to get all entries for given space"

  [req]

  (let [{:keys [space-id]} (:params req)
        opts1 {:headers {"Authorization" (str "Bearer " OWLET-ACTIVITIES-3-MANAGEMENT-AUTH-TOKEN)}}
        opts2 {:headers {"Authorization" (str "Bearer " OWLET-ACTIVITIES-3-DELIVERY-AUTH-TOKEN)}}]
    (let [{:keys [status body]}
          @(http/get (format "https://cdn.contentful.com/spaces/%1s/entries?" space-id) opts2)
          metadata (get-activity-metadata space-id opts1)]
      (if (= status 200)
        (let [entries (json/parse-string body true)
              assets (get-in entries [:includes :Asset])
              platforms (filter-entries "platform" (:items entries))
              activities (filter-entries "activity" (:items entries))]
          (ok {:metadata (process-metadata (:body @metadata))
               :activities (process-activities activities platforms assets)
               :platforms platforms}))
        (not-found status)))))

(defn- compose-new-activity-email
  "Pluck relevant keys from activity payload and return { subject, body }"
  [activity]
  (let [id (-> activity :sys :id)
        title (-> activity :fields :title :en-US)
        author (-> activity :fields :author :en-US)
        image-url (-> activity :fields :preview :sys :url)
        platform-color (-> activity :fields :platform :color)
        platform-name (-> activity :fields :platform :name)
        skills (-> activity :fields :skills :en-US)
        description (-> activity :fields :summary :en-US)
        subject (format "New Owlet Activity Published: %s by %s" title author)
        url (format "http://owlet.codefordenver.org/#/activity/#!%s" id)
        html (render-file "activity-email.html" {:activity-id id
                                                        :activity-image-url image-url
                                                        :activity-title title
                                                        :platform-color platform-color
                                                        :platform-name platform-name
                                                        :activity-description description
                                                        :skill-names skills})]
    (hash-map :subject subject
              :html html)))


(defn handle-confirmation [req]
  (let [id (get-in req [:params :id])
        {:keys [status body]} @(http/get (subscriber-endpoint id))]
    (if (= 200 status)
      (let [subscriber (json/parse-string body true)
            confirmed? (:confirmed subscriber)
            {:keys [status body]}
            @(http/put (subscriber-endpoint id)
                       {:body (json/encode
                                {:email (:email subscriber)
                                 :confirmed (not confirmed?)})})]
        (if (= 200 status)
          (redirect (if confirmed?
                      (str owlet-url "/#/unsubscribed/" (:email subscriber))
                      (str owlet-url "/#/subscribed/" (:email subscriber))))
          (internal-server-error status)))
      (internal-server-error status))))


(defn- send-confirmation-email [email id subscribing]
  "Sends confirmation email"
  (let [url (format "https://owlet.codefordenver.org.herokudns.com/owlet/webhook/content/confirm?id=%1s" id)
        html (if (= subscribing true)
               (render-file "confirm-email.html" {:url url :un ""})
               (render-file "confirm-email.html" {:url url :un "un"}))
        mail-transact!
        (mail/send-mail creds
                        {:from    "owlet@mmmanyfold.com"
                         :to      email
                         :subject "Please confirm your email address"
                         :html    html})]
    (when (= (:status mail-transact!) 200)
      (prn "Sent confirmation email to " email))))

(defn handle-activity-publish
  "Sends email to list of subscribers"
  [req]
  (let [payload (:params req)
        is-new-activity?
        (and (= "activity" (get-in payload [:sys :contentType :sys :id]))
             (= 1 (get-in payload [:sys :revision])))]
    (if is-new-activity?
      (let [{:keys [status body]} @(http/get subscribers-endpoint)]
        (if (= 200 status)
          (let [json (json/parse-string body true)
                users (map val json)
                emails (for [user users :when (:confirmed user)] (:email user))
                subscribers (clojure.string/join "," emails)]
            (let [space-id (get-in payload [:sys :space :sys :id])
                  asset-id (get-in payload [:fields :preview :en-US :sys :id])
                  {:keys [status body]} @(get-asset-by-id space-id asset-id)]
              (if (= 200 status)
                (let [body (json/parse-string body true)
                      asset-url (get-in body [:fields :file :url])
                      payload
                      (-> payload
                          (assoc-in [:fields :preview :sys :url] asset-url))]
                  (let [entry-id (get-in payload [:fields :platformRef :en-US :sys :id])
                        {:keys [status body]} @(get-entry-by-id space-id entry-id)]
                    (if (= 200 status)
                      (let [body (json/parse-string body true)
                            platform-name (get-in body [:fields :name])
                            platform-color (get-in body [:fields :color])
                            payload
                            (-> payload
                                (assoc-in [:fields :platform :name] platform-name)
                                (assoc-in [:fields :platform :color] platform-color))]
                        (let [{:keys [subject html]} (compose-new-activity-email payload)
                              mail-transact!
                              (mail/send-mail creds
                                              {:from    "owlet@mmmanyfold.com"
                                               :to      "owlet@mmmanyfold.com"
                                               :bcc     subscribers
                                               :subject subject
                                               :html    html})]
                          (if (= (:status mail-transact!) 200)
                            (ok "Emailed Subscribers Successfully.")
                            (internal-server-error mail-transact!))))
                      (internal-server-error status))))
                (internal-server-error status))))
          (internal-server-error status)))
      (ok "Not a new activity."))))

(defn handle-activity-subscribe

  "handles new subscription request
   -checks list of subs b4 adding to list; ie no duplicates"

  [req]

  (let [email (-> req :params :email)
        {:keys [status body]} @(http/get subscribers-endpoint)]
    (let [coll (json/parse-string body true)]
      (if (= status 200)
        (if-let [existing-user (some #(when (= (:email %) email) %) (vals coll))]
          (if (:confirmed existing-user)
            (ok "Already subscribed.")
            (let [id (-> (filter (comp #{{:email email :confirmed false}} coll)
                                 (keys coll))
                         first
                         name)]
              (send-confirmation-email email id true)
              (ok "Re-sent confirmation email.")))
          (let [id (epoch)
                {:keys [status body]}
                @(http/put (subscriber-endpoint id)
                           {:body (json/encode {:email email
                                                :confirmed false})})]
            (if (= status 200)
              (do
                (send-confirmation-email email id true)
                (ok "Sent confirmation email."))
              (internal-server-error status))))
        (internal-server-error status)))))

(defn handle-activity-unsubscribe
  "handles unsubscribe request"
  [req]
  (let [email (-> req :params :email)
        {:keys [status body]} @(http/get subscribers-endpoint)]
    (let [coll (json/parse-string body true)]
      (if (= status 200)
        (if-let [existing-user (some #(when (= (:email %) email) %) (vals coll))]
          (if (:confirmed existing-user)
            (let [id (-> (filter (comp #{{:email email :confirmed true}} coll)
                                 (keys coll))
                         first
                         name)]
              (send-confirmation-email email id false)
              (ok "Sent confirmation email."))
            (ok "Not Subscribed.")))
        (internal-server-error)))))

(defroutes routes
           (context "/webhook" []
             (context "/content" []
               (POST "/email" {params :params} handle-activity-publish)
               (PUT "/unsubscribe" {params :params} handle-activity-unsubscribe)
               (PUT "/subscribe" {params :params} handle-activity-subscribe)
               (GET "/confirm" {params :params} handle-confirmation)))
           (context "/content" []
             (GET "/space" {params :params} handle-get-all-entries-for-given-space)))
