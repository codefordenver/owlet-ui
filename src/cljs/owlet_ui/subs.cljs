(ns owlet-ui.subs
  (:require [re-frame.core :as rf]
            [owlet-ui.config :as config]
            [owlet-ui.rf-util :refer [reg-getter]]))


(rf/reg-sub
  :active-view
  (fn [db _]
    (:active-view db)))


(reg-getter :my-id [:my-identity :firebase-id])

(reg-getter :showing-bg-img-upload [:showing-bg-img-upload])

(rf/reg-sub
  :my-background-image-url
  (fn [db]
    (or (get-in db [:my-identity :private :background-image-url])
        config/default-header-bg-image)))

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

(reg-getter :route-params [:app :route-params])
