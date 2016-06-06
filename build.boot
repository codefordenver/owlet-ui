(set-env!
 :source-paths    #{"src/cljs" "less"}
 :resource-paths  #{"resources"}
 :dependencies '[[adzerk/boot-cljs          "1.7.48-6"   :scope "test"]
                 [adzerk/boot-cljs-repl     "0.2.0"      :scope "test"]
                 [adzerk/boot-reload        "0.4.1"      :scope "test"]
                 [pandeiro/boot-http        "0.6.3"      :scope "test"]
                 [org.clojure/clojurescript "1.7.122"]
                 [crisptrutski/boot-cljs-test "0.2.0-SNAPSHOT" :scope "test"]
                 [deraen/boot-less "0.2.1" :scope "test"]
                 [cljsjs/auth0-lock "8.1.5-1"]
                 [cljs-ajax "0.5.4"]
                 [reagent "0.6.0-alpha2"]
                 [reagent-utils "0.1.7"]
                 [reagent-forms "0.5.22"]
                 [secretary "1.2.3"]
                 [kioo "0.4.2"]
                 [re-frame "0.7.0"]
                 [cljs-log "0.2.2"]
                 [devcards "0.2.1-7"]])

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[pandeiro.boot-http    :refer [serve]]
 '[crisptrutski.boot-cljs-test :refer [test-cljs]]
 '[deraen.boot-less    :refer [less]])

(deftask build []
  (comp (speak)
        (cljs)
        (less)
        (sift   :move {#"less.css" "css/less.css" #"less.main.css.map" "css/less.main.css.map"})))

(deftask run
  [p port       PORT int  "Port for web server"]
  (comp (serve :port port)
        (watch)
        (cljs-repl)
        (reload)
        (build)))

(deftask production []
  (task-options! cljs {:optimizations :advanced}
                 less {:compression true})
  identity)

(deftask development []
             (task-options! cljs {:optimizations :none
                                  :source-map true
                                  :closure-defines {"goog.DEBUG" false}}
                            reload {:on-jsload   'owlet.app/mount-components}
                            less   {:source-map  true})
             identity)

(deftask devcard-development []
             (task-options! cljs {:optimizations :none
                                  :source-map true
                                  :compiler-options {:devcards true}
                                  :closure-defines {"goog.DEBUG" false}}
                            reload {:on-jsload   'owlet-devcards.app/init}
                            less   {:source-map  true})
             identity)

(deftask dev
  "Simple alias to run application in development mode"
  [p port       PORT int  "Port for web server"]
  (comp (development)
        (run :port 4000)))

(deftask cards
  "..."
  []
  (comp (devcard-development)
        (run :port 5000)))

(deftask testing []
  (set-env! :source-paths #(conj % "test/cljs"))
  identity)

;;; This prevents a name collision WARNING between the test task and
;;; clojure.core/test, a function that nobody really uses or cares
;;; about.
(ns-unmap 'boot.user 'test)

(deftask test []
  (comp (testing)
        (test-cljs :js-env :phantom
                   :exit?  true)))

(deftask auto-test []
  (comp (testing)
        (watch)
        (test-cljs :js-env :phantom)))
