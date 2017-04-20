(ns owlet-ui.firebase-test
  (:require
    [cljs.test :refer-macros [use-fixtures async deftest testing is]]
    [cljs.core.async :refer [<! chan]]
    [re-frame.core :as rf]
    [re-frame.db :refer [app-db]]
    [owlet-ui.subscription-viewer :as view]
    [owlet-ui.firebase :as fb]
    [owlet-ui.async :as as]
    [owlet-ui.subs :as subs]
    [owlet-ui.events.helpers :refer [reg-setter]])
  (:require-macros
    [cljs.core.async.macros :refer [go-loop]]))


(def init-data (clj->js {:notification "new"
                         :test-time fb/timestamp-placeholder}))


(def test-ref
  "Firebase reference available to all tests. Assigned only in a fixture."
  (atom))


(def test-notify-chan
  "Makes the channel used by handler :test-notify available to change-on-test."
  (atom))


(use-fixtures
  :once

  {:before
   #(async done
      ; Initialize the :tests sandbox in @app-db.
      (swap! app-db assoc :tests {:status "Ready"})

      ; Render our Re-frame view used for testing, complete with the
      ; subscriptions we will need.
      ((view/render-subs-at "app"
                            [:status-sub]
                            [:notification-sub])
       ; Note that the done function below is the ARGUMENT of the function
       ; returned by render-subs-at. Thus, done will be called only when
       ; rendering is complete.
       done))}

  {:before
   #(async done
      ; Push test data onto Firebase, so it exists prior to connecting to it
      ; with on-change.
      (let [tests-ref   (fb/path-str->db-ref "tests")
            on-complete (fn [err]
                          (if err
                            (do (prn err) (flush))
                            (done)))]
        (reset! test-ref
                (.push tests-ref init-data on-complete))))

   :after
   #(print "Done with Firebase.")}

  {:before
   as/repeatedly-run

   :after
   (fn []
     (reset! as/repeatedly-running nil)
     (print "Done with repeatedly-running."))})


(subs/reg-getter :status-sub [:tests :status])
(reg-setter :change-status [:tests :status])


(deftest status-test
  (testing
    "Issuing an event to modify app-db should be reported by the [:status] subscription."
    (async done
      ; Since the ((view/render-subs-at ...) done) call in the fixture above
      ; waits for the render to complete, the data should be available now.
      (is (= "Ready" ((view/results "app") [:status-sub])))

      (rf/dispatch [:change-status "Running"])
      (prn "Waiting for browser ...")
      (go-loop [ch (chan)]
        (as/continue-after-pause! ch)   ; Returns immediately,
        (<! ch)                         ; <-- then waits here.
        (is (= "Running") ((view/results "app") [:status-sub]))
        (done)))))


(deftest test-ref-test
  (testing
    "We should have the correct Firebase database reference."
    (is (not= @test-ref js/undefined))
    (is (= "tests" (-> @test-ref .-parent .-key)))))


(subs/reg-getter :notification-sub [:tests :notification])
(reg-setter
  :test-notify
  [:tests :notification]
  (fn [new-data ch]
    (as/continue-chan! ch)
    new-data))


(deftest on-change-test
  (testing
    "Data on Firebase, whether existing prior to calling on-change to connect,
    or spontaneously modified externally or by the app, should dispatch
    immediately to our :test-notify handler and be propagated to the DOM
    via our :notification-sub subscription. Thus, the app's GUI should be
    initialized and kept up to date with changes to data in Firbase."
    (async done

      ; Establish the connection with the Firebase data of interest, so
      ; any change there will fire our :test-notify event with the new
      ; notification data.
      (go-loop [ch (chan)]
        (fb/on-change (.child @test-ref "notification") :test-notify ch)
        (reset! test-notify-chan ch)    ; change-on-test below uses this chan.

        ; Wait for the :test-notify handler to run. This confirms that we've
        ; received the initial data that already exists on Firebase.
        (<! ch)

        ; Now wait for the GUI to update with the Firebase data.
        (as/continue-after-pause! ch)
        (<! ch)

        (testing
          "The initial Firebase data should appear in the GUI."
          (is (= "new" ((view/results "app") [:notification-sub]))))

        ; Next, let's try directly modifying the data on Firebase to see if
        ; our handler fires and the GUI is updated.
        (.set (.child @test-ref "notification")
              "modified"
              #(as/continue-chan! ch))
        (<! ch)              ; Wait for Firebase to finish.
        (<! ch)              ; Wait for :test-notify handler.

        (as/continue-after-pause! ch)
        (<! ch)              ; Again, wait for the GUI.

        (testing
          "Modifying data on Firebase should be reflected in the GUI."
          (is (= "modified" ((view/results "app") [:notification-sub]))))

        (done)))))


(subs/reg-getter :upload-sub [:tests :upload])


(deftest change-on-test
  (testing
    "Function change-on should subscribe to a given subscription listening for
    changes to app-db, and should update Firebase at the given ref."
    (async done
      (go-loop [ch       @test-notify-chan
                uploader (fb/change-on (.child @test-ref "notification")
                                       :upload-sub)]
        ; To detect that our :upload-sub has been triggered, we make use of
        ; the on-change-test setup above, which is still in effect. Here's
        ; how the round-trip will work: Any change in the :upload value in
        ; :tests in @app-db (initially nil) should be noticed by subscription
        ; :upload-sub. Change-on subscribed to :upload-sub and will react by
        ; uploading the :upload value to Firebase into node "notifications".
        ; As in on-change-test, above, this change on Firebase will cause
        ; event :test-notify to fire and update the GUI with the new Firebase
        ; value, which we will check.

        ; Wait for the :test-notify handler.
        (<! ch)
        ; Wait for the GUI.
        (as/continue-after-pause! ch)
        (<! ch)

        (try
          (is (nil? ((view/results "app") [:notification-sub])))

          (swap! app-db assoc-in [:tests :upload] "uploaded")
          ; Wait for the :test-notify handler.
          (<! ch)
          ; Wait for the GUI.
          (as/continue-after-pause! ch)
          (<! ch)
          (is (= "uploaded" ((view/results "app") [:notification-sub])))

          (finally
            (swap! as/repeatedly-running disj uploader)
            (done)))))))


(deftest status-finished
  (testing
    "Issuing an event to modify app-db should be reported by the [:status] subscription."
    (async done
      (is (= "Running" ((view/results "app") [:status-sub])))

      (rf/dispatch [:change-status "Finished"])
      (go-loop [ch (chan)]
        (as/continue-after-pause! ch)
        (<! ch)
        (is (= "Finished") ((view/results "app") [:status-sub]))

        (print "Finished testing.")
        (done)))))
