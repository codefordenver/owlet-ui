(ns ^:figwheel-no-load owlet.app
  (:require [owlet.core :as core]
            [devtools.core :as devtools]))

(enable-console-print!)

(devtools/install!)

(core/init!)
