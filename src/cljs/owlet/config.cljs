(ns owlet.config)

(def debug?
  ^boolean js/goog.DEBUG)

;; TODO:
;; Use a lein env var like the one above
;; to toggle this during development
;; http://localhost:3000
(def server-url
  "http://owlet-api.apps.aterial.org")

(when debug?
      (enable-console-print!))