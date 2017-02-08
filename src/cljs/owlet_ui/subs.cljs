(ns owlet-ui.subs
  (:require [re-frame.core :as re]))


(defn register-getter-sub
  "Provides an easy way to register a new subscription that just retrieves the
  value from a given location in app-db. Simply provide the query-key keyword
  designating the new subscription and the path in app-db to the new data of
  interest. Optionally, the value returned by the subscription's reaction may
  be the result of a function you provide a that takes as arguments the new
  value and elements in the subscription vector following the query key. For
  example, if we called

    (register-getter-sub :my-sub
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
   (register-getter-sub query-key db-path identity))

  ([query-key db-path f]
   (re/reg-sub
     query-key
     (fn [db _ & args]
       (-> db
           (get-in db-path)
           (#(apply f % args)))))))

(re/reg-sub
  :active-view
  (fn [db _]
    (:active-view db)))

(re/reg-sub
  :is-user-logged-in?
  (fn [db]
    (get-in db [:user :logged-in?])))

(re/reg-sub
  :social-id-subscription
  (fn [db]
    (get-in db [:user :social-id])))

(re/reg-sub
  :user-has-background-image?
  (fn [db]
    (get-in db [:user :background-image])))

(re/reg-sub
  :library-activities
  (fn [db]
    (get-in db [:activities])))

(re/reg-sub
  :activity-branches
  (fn [db]
    (get-in db [:activity-branches])))

(re/reg-sub
  :activities-by-branch
  (fn [db]
    (get-in db [:activities-by-branch])))

(re/reg-sub
  :activities-by-branch-in-view
  (fn [db]
    (get-in db [:activities-by-branch-in-view])))

(re/reg-sub
  :activity-in-view
  (fn [db]
    (get-in db [:activity-in-view])))

(re/reg-sub
  :set-loading-state?
  (fn [db]
    (get-in db [:app :loading?])))

(register-getter-sub :app-title [:app :title])

(register-getter-sub :skills [:skills])

(register-getter-sub :activity-titles [:activity-titles])