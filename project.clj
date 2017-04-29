(defproject owlet-ui "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0-alpha15"]
                 [org.clojure/clojurescript "1.9.456"]
                 [org.clojure/tools.nrepl "0.2.13"]   ; Dirac needs recent vers.
                 [binaryage/devtools "RELEASE"]
                 [binaryage/dirac "RELEASE"]
                 [re-frame "0.9.2"]
                 [secretary "1.2.3"]
                 [compojure "1.5.2"]
                 [yogthos/config "0.8"]       ; For env in owlet-ui.server
                 [ring "1.4.0"]               ; For owlet-ui.server & .handler
                 [cljsjs/jquery "2.2.2-0"]
                 [cljs-ajax "0.5.4"]
                 [cljsjs/auth0 "7.0.4-0"]
                 [cljsjs/auth0-lock "10.4.0-0"]
                 [cljsjs/bootstrap "3.3.6-1"]
                 [cljsjs/firebase "3.5.3-1"]
                 [cljsjs/showdown "1.4.2-0"]
                 [org.clojure/core.async "0.3.442"]
                 [camel-snake-kebab "0.4.0"]
                 [cljsjs/google-analytics "2015.04.13-0"]
                 [cljsjs/photoswipe "4.1.1-0"]
                 [re-com "1.0.0"]
                 [re-frisk "0.4.4"]
                 [day8.re-frame/http-fx "0.1.3"]]

  :plugins [[lein-cljsbuild "1.1.5"]
            [lein-less "1.7.5"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"
                                    "test/js"]

  :figwheel {:css-dirs ["resources/public/css"]
             :ring-handler owlet-ui.handler/dev-handler
             :server-port 4000}

  :less {:source-paths ["less"]
         :target-path  "resources/public/css"}

  :profiles
  {:dev
   {:dependencies [[figwheel-sidecar "0.5.10"]]
    :source-paths ["src/cljs" "test/cljs"]  ; Needed to run Figwheel from nrepl.
    :plugins      [[lein-doo "0.1.6"]]}

   :repl
   {:repl-options {:port 8230}}

   :figwheel    ; Abitrary key, used in `lein with-profile +figwheel repl`.
   {:dependencies [[com.cemerick/piggieback "0.2.1"]]
    :repl-options {:nrepl-middleware
                   [cemerick.piggieback/wrap-cljs-repl]

                   :timeout
                   600000         ; Wait up to 10 minutes for compilation.

                   :init
                   (do (require 'figwheel-sidecar.repl-api)
                       (figwheel-sidecar.repl-api/start-figwheel!))

                   :welcome
                   (do (println "\n\n                --- Using the Figwheel REPL --\n")
                       (println "This is a ClojureScript REPL, not Clojure. For a Clojure REPL, enter :cljs/quit")
                       (println "\n                ---          Enjoy!         --\n\n")
                       (figwheel-sidecar.repl-api/cljs-repl))}}

   :dirac       ; Abitrary key, used in `lein with-profile +dirac repl`.
   {:repl-options {:nrepl-middleware
                   [dirac.nrepl/middleware]

                   :timeout
                   180000

                   :init
                   (do (require 'figwheel-sidecar.repl-api)
                       (figwheel-sidecar.repl-api/start-figwheel!)
                       (require 'dirac.agent)
                       (dirac.agent/boot!))

                   :welcome
                   (do (println "\n\n                --- Using the Dirac DevTools REPL --\n")
                       (println "This is a Clojure REPL, not ClojureScript. For a ClojureScript REPL, open")
                       (println "http://localhost:4000/ in Chrome Canary with extension Dirac DevTools installed.")
                       (println "Now click the Dirac DevTools icon to the right of the address bar.")
                       (println "Do NOT use the regular Chrome DevTools (\"Developer Tools\", Cmd-Opt-I).")
                       (println "You can then also join that browser REPL session here at this command line")
                       (println "by evaluating (dirac! :join) at the prompt below.")
                       (println "\n                ---             Enjoy!            --\n\n"))}}}

  :cljsbuild
  {:builds
   [{:id             "dev"
     :notify-command ["script/notify_command.sh"]
     :source-paths   ["src/cljs" "test/cljs"]
     :figwheel       {:on-jsload "owlet-ui.core/mount-root"}
     :compiler       {:main                 owlet-ui.core
                      :output-to            "resources/public/js/compiled/app.js"
                      :output-dir           "resources/public/js/compiled/out"
                      :asset-path           "js/compiled/out"
                      :source-map-timestamp true}}

    {:id           "min"
     :source-paths ["src/cljs"]
     :jar          true
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

  :aot [owlet-ui.server])

