(ns owlet-ui.firebase-test
  (:require
    [cljs.test :refer-macros [use-fixtures async deftest testing is]]
    [cljs.core.async :refer [chan close! <!]]
    [re-frame.core :as re]
    [re-frame.db :refer [app-db]]
    [owlet-ui.testing-data-flow :as tdf]
    [owlet-ui.firebase :as fb])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))


(def test-chan (chan))
(def test-ref (atom))
(def init-data (clj->js {:notification "new"
                         :test-time fb/timestamp-placeholder}))


(use-fixtures
  :once

  {:before
   #(async done
      ; Initialize the :tests sandbox in @app-db.
      (swap! app-db assoc :tests {:status "Ready"})

      ; Render our Re-frame view used for testing, complete with the
      ; subscriptions we will need.
      ((tdf/render-subs-at "app"
                           [:status-sub]
                           [:notification-sub])
       ; Note that the done function below is the ARGUMENT of the function
       ; returned by render-subs-at. Thus, done will be called only when
       ; rendering is complete.
       done))

   :after
   (fn []
     (swap! app-db update-in [] dissoc :tests)
     (close! test-chan))}

  {:before
   #(async done
     ; Push test data onto Firebase, so it exists prior to connecting to it
     ; with on-change.
     (let [tests-ref    (fb/ref-for-path "tests")
           on-complete  (fn [err]
                          (if err
                            (do (prn err) (flush))
                            (done)))]
       (reset! test-ref
               (.push tests-ref init-data on-complete))))

   :after
   #(print "Done with Firebase.")})


(tdf/register-getter-sub :status-sub [:tests :status])
(tdf/register-setter-handler :change-status [:tests :status])


(deftest status-test
  (testing
    "Issuing an event to modify app-db should be reported by the [:status] subscription."
    (async done
      (is (= "Ready" ((tdf/results) [:status-sub])))
      (re/dispatch [:change-status "Running"])
      (prn "Waiting for browser ...")
      (go
        (tdf/continue-after-pause! test-chan)   ; Returns immediately,
        (<! test-chan)                          ; <-- then waits here.
        (is (= "Running") ((tdf/results) [:status-sub]))
        (done)))))


(tdf/register-getter-sub :notification-sub [:tests :notification])
(tdf/register-setter-handler
  :test-notify
  [:tests :notification]
  (fn [new-data]
    (tdf/continue-chan! test-chan)
    new-data))


(deftest change-on-test
  (testing
    "Data on Firebase, whether existing prior to calling on-change to connect,
    or spontaneously modified externally or by the app, should dispatch
    immediately to our :test-notify handler and be propagated to the DOM
    via our :notification-sub subscription. Thus, the app's GUI should be
    initialized and kept up to date with changes to data in Firbase."
    (async done
      (testing
        "We should have the correct Firebase database reference."
        (is (not= @test-ref js/undefined))
        (is (= "tests" (-> @test-ref .-parent .-key))))

      ; Establish the connection with the Firebase data of interest, so
      ; any change there will fire our :test-notify event with the new
      ; notification data.
      (fb/on-change (.child @test-ref "notification") :test-notify)

      (go
        ; Wait for the :test-notify handler to run. This confirms that we've
        ; received the initial data that already exists on Firebase.
        (<! test-chan)

        ; Now wait for the GUI to update with the Firebase data.
        (tdf/continue-after-pause! test-chan)
        (<! test-chan)

        (testing
          "The initial Firebase data should appear in the GUI."
          (is (= "new" ((tdf/results) [:notification-sub]))))

        ; Next, let's try directly modifying the data on Firebase to see if
        ; our handler fires and the GUI is updated.
        (.set (.child @test-ref "notification")
              "modified"
              #(tdf/continue-chan! test-chan))
        (<! test-chan)          ; Wait for Firebase to finish.

        ; Again, wait for the GUI.
        (tdf/continue-after-pause! test-chan)
        (<! test-chan)

        (testing
          "Modifying data on Firebase should be reflected in the GUI."
          (is (= "modified" ((tdf/results) [:notification-sub]))))

        (done)))))


(deftest status-finished
  (testing
    "Issuing an event to modify app-db should be reported by the [:status] subscription."
    (async done
      (is (= "Running" ((tdf/results) [:status-sub])))
      (re/dispatch [:change-status "Finished"])
      (go
        (tdf/continue-after-pause! test-chan)   ; Returns immediately,
        (<! test-chan)                          ; <-- then waits here.
        (is (= "Finished") ((tdf/results) [:status-sub]))
        (print "Finished testing.")
        (done)))))

