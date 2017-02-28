(ns owlet-ui.subs
  (:require [re-frame.core :as rf]
            [owlet-ui.config :as config]))


(defn reg-getter
  "Provides an easy way to register a new subscription that just retrieves the
  value from a given location in app-db. Simply provide the query-key keyword
  designating the new subscription and the path in app-db to the new data of
  interest. Optionally, the value returned by the subscription's reaction may
  be the result of a function you provide a that takes as arguments the new
  value and elements in the subscription vector following the query key. For
  example, if we called

    (reg-getter :my-sub
                [:path :in :app-db]
                (fn [new-val up?]
                  (if up? (upper-case new-val) new-val)))

    (defn some-component []
      (let [the-word (subscribe [:my-sub true])]
        (fn ...

  Now, after executing something like

    (swap! app-db assoc-in [:path :in :app-db] \"new words\")

  some-component will render using \"NEW WORDS\" as the value from @the-word."

  ([query-key db-path]
   (reg-getter query-key db-path identity))

  ([query-key db-path f]
   (rf/reg-sub
     query-key
     (fn [db _ & args]
       (-> db
           (get-in db-path)
           (#(apply f % args)))))))


(rf/reg-sub
  :active-view
  (fn [db _]
    (:active-view db)))


(reg-getter :my-identity [:my-identity])

(reg-getter :showing-bg-img-upload [:showing-bg-img-upload])

(rf/reg-sub
  :my-background-image-url
  (fn [db]
    (let [me (rf/subscribe [:my-identity])]
      (or
        (get-in db [:users (:firebase-id @me) :background-image-url])
        config/default-header-bg-image))))

(rf/reg-sub
  :library-activities
  (fn [db]
    (get-in db [:activities])))

(rf/reg-sub
  :activity-branches
  (fn [db]
    (get-in db [:activity-branches])))

(rf/reg-sub
  :activities-by-branch
  (fn [db]
    (get-in db [:activities-by-branch])))

(rf/reg-sub
  :activities-by-branch-in-view
  (fn [db]
    (get-in db [:activities-by-branch-in-view])))

(rf/reg-sub
  :activity-in-view
  (fn [db]
    (get-in db [:activity-in-view])))

(rf/reg-sub
  :set-loading-state?
  (fn [db]
    (get-in db [:app :loading?])))

(reg-getter :app-title [:app :title])

(reg-getter :open-sidebar? [:app :open-sidebar])

(reg-getter :skills [:skills])

(reg-getter :activity-titles [:activity-titles])

(reg-getter :activity-platforms [:activity-platforms])
