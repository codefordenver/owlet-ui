(defproject owlet-ui "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.8.51"]
                 [reagent "0.5.1"]
                 [binaryage/devtools "0.6.1"]
                 [re-frame "0.7.0"]
                 [secretary "1.2.3"]
                 [compojure "1.5.0"]
                 [yogthos/config "0.8"]
                 [ring "1.4.0"]
                 [cljsjs/jquery "2.2.2-0"]
                 [cljs-ajax "0.5.4"]
                 [cljsjs/auth0-lock "8.1.5-1"]
                 [reagent-utils "0.1.7"]
                 [devcards "0.2.1-7"]
                 [figwheel-sidecar "0.5.8"]
                 [cljsjs/firebase "3.2.1-0"]
                 [cljs-react-test "0.1.3-SNAPSHOT"]
                 [org.clojure/core.async "0.2.385"]]

  :plugins [[lein-cljsbuild "1.1.3"]
            [lein-less "1.7.5"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "script"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"
                                    "test/js"]

  :figwheel {:css-dirs ["resources/public/css"]
             :ring-handler owlet-ui.handler/dev-handler
             :server-port 4000}

  :less {:source-paths ["less"]
         :target-path  "resources/public/css"}

  :profiles
  {:dev
   {:dependencies []

    :plugins      [[lein-doo "0.1.6"]]}}


  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs" "test/cljs"]
     :figwheel     {:on-jsload "owlet-ui.core/mount-root"}
     :compiler     {:main                 owlet-ui.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true}}

    {:id           "min"
     :source-paths ["src/cljs"]
     :jar true
     :compiler     {:main            owlet-ui.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}

    {:id           "test"
     :source-paths ["src/cljs" "test/cljs"]
     :compiler     {:output-to     "resources/public/js/compiled/test.js"
                    :output-dir    "resources/public/js/compiled/test-out"
                    :main          owlet-ui.runner
                    :optimizations :none}}]}


  :main owlet-ui.server

  :aot [owlet-ui.server]

  :prep-tasks [["cljsbuild" "once" "min"] "compile"])
