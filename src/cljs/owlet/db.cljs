(ns owlet.db)

(def default-db
  "init app-state"
  {:user {:logged-in?       false
          :social-id        nil
          :content-entries  []
          :background-image nil}})