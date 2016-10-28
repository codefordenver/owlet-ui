#!/usr/bin/env bash
#
# This script just employs Leiningen to run a Figwheel REPL server. It is run
# from the command line to compile the ClojureScript app and run the Figwheel
# auto-compilation and REPL environment. It is necessary because the the
# lein-figwheel plugin is incompatible with the configuration for running
# Figwheel in a Cursive Clojure REPL, as explained here:
#
# https://github.com/bhauman/lein-figwheel/wiki/Running-figwheel-in-a-Cursive-Clojure-REPL
#
#   -- Tyler Perkins, 08-15-2016
#

here="${0%/*}"

lein clean && rlwrap lein run -m clojure.main "$here/repl.clj"
