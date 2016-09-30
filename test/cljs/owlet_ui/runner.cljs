(ns owlet-ui.runner
    (:require [owlet-ui.core]
              [doo.runner :refer-macros [doo-tests]]
              owlet-ui.core-test
              owlet-ui.firebase-test))


(doo-tests
  'owlet-ui.core-test
  'owlet-ui.firebase-test)
