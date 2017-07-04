(defproject owlet "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
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
                 [day8.re-frame/http-fx "0.1.3"]
                 [ch.qos.logback/logback-classic "1.1.7"]
                 [clj-time "0.13.0"]
                 [cljs-ajax "0.6.0"]
                 [compojure "1.6.0"]
                 [cprop "0.1.10"]
                 [funcool/struct "1.0.0"]
                 [luminus-http-kit "0.1.4"]
                 [luminus-nrepl "0.1.4"]
                 [luminus/ring-ttl-session "0.3.2"]
                 [markdown-clj "0.9.99"]
                 [metosin/compojure-api "1.1.10"]
                 [metosin/muuntaja "0.3.1"]
                 [metosin/ring-http-response "0.9.0"]
                 [mount "0.1.11"]
                 [org.clojure/clojurescript "1.9.671" :scope "provided"]
                 [org.clojure/tools.cli "0.3.5"]
                 [org.clojure/tools.logging "0.4.0"]
                 [org.webjars.bower/tether "1.4.0"]
                 [org.webjars/bootstrap "4.0.0-alpha.5"]
                 [org.webjars/font-awesome "4.7.0"]
                 [reagent "0.7.0"]
                 [reagent-utils "0.2.1"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.6.1"]
                 [ring/ring-defaults "0.3.0"]
                 [secretary "1.2.3"]
                 [selmer "1.10.7"]]


  :jvm-opts ["-server" "-Dconf=.lein-env"]

  :plugins [[lein-less "1.7.5"]
            [lein-cprop "1.0.3"]
            [lein-cljsbuild "1.1.5"]
            [lein-sassc "0.10.4"]
            [lein-auto "0.1.2"]
            [lein-kibit "0.1.2"]]

  ;; TODO: use this to run lein less auto insted
  ;:auto
  ;{"sassc" {:file-pattern #"\.(scss|sass)$" :paths ["resources/scss"]}}

  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "src/cljc"]
  :test-paths ["test/clj"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main ^:skip-aot owlet.core

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"
                                    "test/js"]

  :figwheel
  {:http-server-root "public"
   :nrepl-port 7002
   :css-dirs ["resources/public/css"]
   :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :less {:source-paths ["less"]
         :target-path  "resources/public/css"}

  :profiles
  {
   :project/dev  {:dependencies [[prone "1.1.4"]
                                     [ring/ring-mock "0.3.0"]
                                     [ring/ring-devel "1.6.1"]
                                     [pjstadig/humane-test-output "0.8.2"]
                                     [binaryage/devtools "0.9.4"]
                                     [com.cemerick/piggieback "0.2.2"]
                                     [doo "0.1.7"]
                                     [figwheel-sidecar "0.5.10"]]
                      :plugins      [[com.jakemccrary/lein-test-refresh "0.19.0"]
                                     [lein-doo "0.1.7"]
                                     [lein-figwheel "0.5.10"]
                                     [org.clojure/clojurescript "1.9.671"]]
                      :cljsbuild
                                    {:builds
                                     {:app
                                      {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                                       :figwheel {:on-jsload "owlet.core/mount-components"}
                                       :compiler
                                                     {:main "owlet.app"
                                                      :asset-path "/js/out"
                                                      :output-to "target/cljsbuild/public/js/app.js"
                                                      :output-dir "target/cljsbuild/public/js/out"
                                                      :source-map true
                                                      :optimizations :none
                                                      :pretty-print true}}}}



                      :doo {:build "test"}
                      :source-paths ["env/dev/clj"]
                      :resource-paths ["env/dev/resources"]
                      :repl-options {:init-ns user}
                      :injections [(require 'pjstadig.humane-test-output)
                                   (pjstadig.humane-test-output/activate!)]}
   :project/test {:resource-paths ["env/test/resources"]
                  :cljsbuild
                                  {:builds
                                   {:test
                                    {:source-paths ["src/cljc" "src/cljs" "test/cljs"]
                                     :compiler
                                                   {:output-to "target/test.js"
                                                    :main "owlet.doo-runner"
                                                    :optimizations :whitespace
                                                    :pretty-print true}}}}}
   :uberjar {:omit-source true
             :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
             :cljsbuild
                          {:builds
                           {:min
                            {:source-paths ["src/cljc" "src/cljs" "env/prod/cljs"]
                             :compiler
                                           {:output-to "target/cljsbuild/public/js/app.js"
                                            :optimizations :advanced
                                            :pretty-print false
                                            :closure-warnings
                                            {:externs-validation :off :non-standard-jsdoc :off}
                                            :externs ["react/externs/react.js"]}}}}


             :aot :all
             :uberjar-name "owlet.jar"
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   ;:dev
   ;{:dependencies [[figwheel-sidecar "0.5.10"]]
   ; :source-paths ["src/cljs" "test/cljs"]  ; Needed to run Figwheel from nrepl.
   ; :plugins      [[lein-doo "0.1.6"]]}
   :profiles/dev {}
   :profiles/test {}
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
                    :optimizations :none}}]})

