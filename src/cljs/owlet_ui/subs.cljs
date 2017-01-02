(ns owlet-ui.subs
  (:require [re-frame.core :as re])
  (:require-macros [reagent.ratom :refer [reaction]]))


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
   (re/register-sub
     query-key
     (fn [db _ & args]
       (-> @db
           (get-in db-path)
           (#(apply f % args))
           reaction)))))


(re/register-sub
 :active-view
 (fn [db _]
   (reaction (:active-view @db))))


(re/register-sub
 :is-user-logged-in?
 (fn [db]
   (reaction (get-in @db [:user :logged-in?]))))


(re/register-sub
 :social-id-subscription
 (fn [db]
   (reaction (get-in @db [:user :social-id]))))


(re/register-sub
 :user-has-background-image?
 (fn [db]
     (reaction (get-in @db [:user :background-image]))))


(re/register-sub
  :library-activities
  (fn [db]
    (reaction (get-in @db [:activities]))))

(re/register-sub
  :activity-branches
  (fn [db]
    (reaction (get-in @db [:activity-branches]))))

(re/register-sub
  :activities-by-branch
  (fn [db]
    (reaction (get-in @db [:activities-by-branch]))))

(re/register-sub
  :activities-by-branch-in-view
  (fn [db]
    (reaction (get-in @db [:activities-by-branch-in-view]))))

(re/register-sub
  :activity-in-view
  (fn [db]
    (reaction (get-in @db [:activity-in-view]))))

(re/register-sub
  :set-loading-state?
  (fn [db]
    (reaction (get-in @db [:app :loading?]))))
