(ns owlet-ui.testing-data-flow
  [:require
            [cljs.reader :refer [read-string]]
            [reagent.core :refer [render]]
            [reagent.ratom :refer-macros [reaction]]
            [re-frame.core :as re]
            [re-frame.router :refer [run-after-next-annimation-frame]]
            [cljs.core.async :refer [put! <!]]])


(defn component-with-subscriptions
  [& subscribe-vecs]
  (fn []
    (let [sub-reactions (map re/subscribe subscribe-vecs)
          sub-count     (count sub-reactions)
          indexed-subs  (map vector (range) subscribe-vecs sub-reactions)
          id->span-id   (fn [key n] (keyword (str "span#" key n)))]

      (fn [] (apply vector
                    :pre

                    [:div "Results from "
                     [:span#count sub-count]
                     " subscription"
                     (if (= 1 sub-count) "" "s")
                     ":\n\n"]

                    (for [[id sub-vec sub-reaction] indexed-subs]
                      [:div [(id->span-id "key" id) (str sub-vec)]
                       " --> "
                       [(id->span-id "val" id)
                        (pr-str @sub-reaction)]]))))))


(defn result-at-id
  [& id-vals]
  (-> js/document
      (.getElementById (apply str id-vals))
      .-innerHTML
      read-string))


(defn results
  ([]  (into (array-map) (for [n (range (result-at-id "count"))]
                           [(result-at-id "key" n)
                            (result-at-id "val" n)])))
  ([n] (result-at-id "val" n)))


(defn register-getter-sub
  ([query-key db-path]
   (register-getter-sub query-key db-path identity))

  ([query-key db-path f]
   (re/register-sub
     query-key
     (fn [db _]
       (-> @db (get-in db-path) f reaction)))))


(defn register-setter-handler
  ([event-key db-path]
   (register-setter-handler event-key db-path identity))

  ([event-key db-path f]
   (re/register-handler
     event-key
     (fn [db [_ new-data]]
       (assoc-in db db-path (f new-data))))))


(defn render-subs-at
  [dom-id & subscribe-vecs]
  (partial render
           [(apply component-with-subscriptions subscribe-vecs)]
           (.getElementById js/document dom-id)))


(defn continue-chan!
  [ch]
  (put! ch :continue))


(defn continue-after-pause!
  [ch]
  (run-after-next-annimation-frame
    (fn [] (run-after-next-annimation-frame #(continue-chan! ch)))))

