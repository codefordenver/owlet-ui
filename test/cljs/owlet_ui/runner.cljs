(ns owlet-ui.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [owlet-ui.core-test]))

(doo-tests 'owlet-ui.core-test)
