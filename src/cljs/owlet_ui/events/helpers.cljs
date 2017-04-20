(ns owlet-ui.events.helpers
  (:require [re-frame.core :as rf]))

(defn reg-setter
  "Provides an easy way to register a new handler returning a map that differs
  from the given one only at the location at the given path vector. Simply
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
