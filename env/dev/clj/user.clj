(ns user
  (:require [mount.core :as mount]
            [owlet.figwheel :refer [start-fw stop-fw cljs]]
            owlet.core))

(defn start []
  (mount/start-without #'owlet.core/repl-server))

(defn stop []
  (mount/stop-except #'owlet.core/repl-server))

(defn restart []
  (stop)
  (start))


