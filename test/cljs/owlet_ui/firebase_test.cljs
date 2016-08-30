(ns owlet-ui.firebase-test
  (:require
    [cljs.test :refer-macros [use-fixtures async deftest testing is]]
    [reagent.core :refer [render]]
    [reagent.ratom :refer-macros [reaction]]
    [re-frame.db :refer [app-db]]
    [re-frame.core :as re]
    [cljs-react-test.utils :as tu]
    [owlet-ui.app :as app]
    [owlet-ui.firebase :as fb]
    [cljs.core.async :refer [<! >! chan]])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))


(def main-component (tu/new-container!))
(def ^:dynamic test-ref)
(def init-data (clj->js {:notification "new"
                         :test-time fb/timestamp-placeholder}))


(use-fixtures :once

  {:before
   #(async done
           (let [tests-ref    (fb/ref-for-path "tests")
                 uploaded-ref (atom)
                 on-complete  (fn [err]
                                (if err
                                  (do (prn err) (flush))
                                  (binding [test-ref @uploaded-ref]
                                    (done))))]
             (reset! uploaded-ref
                     (.push tests-ref init-data on-complete))))
   :after
   #(print "Finished with Firebase.")}

  {:before
   #(do (render [app/main-view] main-component)
        (dissoc @app-db :tests))
   :after
   #(do (dissoc @app-db :tests)
        (tu/unmount! main-component))})


(re/register-sub
  :tests-notification
  (fn [db _]
    (reaction (get-in @db [:tests :notification]))))


(re/register-handler
  :test-notify
  (fn [db [_ notification-msg]]
    (assoc-in @db [:tests :notification] notification-msg)))


(deftest setup-test
  (testing "The Firebase ref used for testing must be defined."
    (is (not= test-ref js/undefined))
    (is (= "tests" (-> test-ref .-parent .-key)))))


(deftest change-on-test
  (is (nil? "Testing the test. This should fail.")))

