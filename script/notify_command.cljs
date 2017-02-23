#!/usr/bin/env planck
(ns notify-command.core
  (:require [planck.shell :refer [sh with-sh-dir]]))

(defn check-for-owlet-ready-config []
      (let [{:keys [out]} (with-sh-dir "~" (sh "ls" "-a" "."))
            lines (clojure.string/split out "\n")
            found (some #(= ".owlet_repl_ready" %) lines)]
           (when found
                 (with-sh-dir "~"
                   (sh "./owlet_repl_ready")))))


(defn -main []
      (check-for-owlet-ready-config))

(-main)
