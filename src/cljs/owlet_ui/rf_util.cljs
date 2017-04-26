(ns owlet-ui.rf-util
  "Useful extensions to the re-frame framework, usable in any re-frame app.
  "
  (:require [re-frame.core :as rf]))


(defn reg-setter
  "Provides an easy way to register a new handler returning a map that differs
  from the given db map only at the location at the given path vector. Simply
  provide the event-key keyword and the db-path vector. Optionally, the new
  value may be the result of a function you provide a that takes as arguments
  the new value and any values following the new value in the vector given to
  the handler. For example, if we called

    (register-setter-handler :my-handler
                             [:path :in :app-db]
                             (fn [new-val up?]
                               (if up? (upper-case new-val) new-val)))

    (dispatch [:my-handler \"new words\" true])

  Now, evaluating

    (get-in @app-db [:path :in :app-db])

  will result in \"NEW WORDS\".
  "

  ([event-key db-path]
   (reg-setter event-key db-path identity))

  ([event-key db-path f]
   (rf/reg-event-db
     event-key
     (fn [db [_ new-data & args]]
       (assoc-in db db-path (apply f new-data args))))))


(defn reg-getter
  "Provides an easy way to register a new subscription that just retrieves the
  value in the given db map at the given path. Simply provide the query-key
  keyword designating the new subscription and the path in the db map to the
  data of interest. Optionally, the value returned by the subscription's
  reaction may be the result of a function you provide a that takes as arguments
  the new value and elements in the subscription vector following the query key.
  For example, if we called

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

