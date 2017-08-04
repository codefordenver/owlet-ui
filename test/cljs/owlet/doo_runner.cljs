(ns owlet.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [owlet.core-test]))

(doo-tests 'owlet.core-test)

