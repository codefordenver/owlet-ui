(defproject owlet "0.1.0-SNAPSHOT"

  :description "Code For Denver: Owlet Project"
  :url "codefordenver.org"

  :dependencies [[ch.qos.logback/logback-classic "1.1.7"]
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
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.671" :scope "provided"]
                 [org.clojure/tools.cli "0.3.5"]
                 [org.clojure/tools.logging "0.4.0"]
                 [org.webjars.bower/tether "1.4.0"]
                 [org.webjars/bootstrap "4.0.0-alpha.5"]
                 [org.webjars/font-awesome "4.7.0"]
                 [re-frame "0.9.4"]
                 [reagent "0.7.0"]
                 [reagent-utils "0.2.1"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.6.1"]
                 [ring/ring-defaults "0.3.0"]
                 [secretary "1.2.3"]
                 [selmer "1.10.8"]
                 [org.clojure/clojure "1.9.0-alpha15"]
                 [org.clojure/clojurescript "1.9.456"]
                 [org.clojure/tools.nrepl "0.2.13"]   ; Dirac needs recent vers.
                 [binaryage/devtools "RELEASE"]
                 [binaryage/dirac "RELEASE"]
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
                 [ring-middleware-format "0.7.0"]
                 [ring-webjars "0.1.1"]
                 [ring-cors "0.1.8"]
                 [ring/ring-json "0.4.0"]
                 [cheshire "5.6.3"]
                 [environ "1.0.2"]
                 [metosin/compojure-api "1.1.9"]
                 [nilenso/mailgun "0.2.3"]]

  :min-lein-version "2.0.0"

  :jvm-opts ["-server" "-Dconf=.lein-env"]
  :source-paths ["src/clj" "src/cljc"]
  :test-paths ["test/clj"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main ^:skip-aot owlet.core

  :plugins [[lein-cprop "1.0.3"]
            [lein-cljsbuild "1.1.5"]
            [lein-sassc "0.10.4"]
            [lein-auto "0.1.2"]
            [lein-kibit "0.1.2"]]

  :sassc [{:src "resources/scss/site.scss"
           :output-to "resources/public/css/site.css"
           :style "nested"
           :import-path "resources/scss"}]

  :auto {"sassc"  {:file-pattern  #"\.(scss)$"
                   :paths ["resources/scss"]}}

  :clean-targets ^{:protect false}
  [:target-path [:cljsbuild :builds :app :compiler :output-dir] [:cljsbuild :builds :app :compiler :output-to]]
  :figwheel
  {:http-server-root "public"
   :nrepl-port 7002
   :css-dirs ["resources/public/css"]
   :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}


  :profiles
  {:uberjar {:omit-source true
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

   :project/dev  {:dependencies [[prone "1.1.4"]
                                 [ring/ring-mock "0.3.1"]
                                 [ring/ring-devel "1.6.1"]
                                 [pjstadig/humane-test-output "0.8.2"]
                                 [binaryage/devtools "0.9.4"]
                                 [com.cemerick/piggieback "0.2.2"]
                                 [doo "0.1.7"]
                                 [figwheel-sidecar "0.5.11"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.19.0"]
                                 [lein-doo "0.1.7"]
                                 [lein-figwheel "0.5.11"]
                                 [org.clojure/clojurescript "1.9.671"]]
                  :cljsbuild
                  {:builds
                   {:app
                    {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                     :figwheel {:on-jsload "owlet.core/mount-root"}
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


   :profiles/dev {}
   :profiles/test {}})
