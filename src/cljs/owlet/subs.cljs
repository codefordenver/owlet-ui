(ns owlet.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]))

;; -- Subscription Handlers ---------------------------------------------------

(re-frame/register-sub
  :is-user-logged-in?
  (fn [db]
      (reaction (get-in @db [:user :logged-in?]))))

(re-frame/register-sub
  :social-id-subscription
  (fn [db]
      (reaction (get-in @db [:user :social-id]))))

(re-frame/register-sub
  :user-has-background-image?
  (fn [db]
      (reaction (get-in @db [:user :background-image-entry]))))