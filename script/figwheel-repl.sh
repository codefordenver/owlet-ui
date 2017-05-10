#!/usr/bin/env bash
#
#
#   -- Tyler Perkins, 12-30-2016
#

USAGE="
Usage:  $0 [--][no-readline | no-clean | dirac | help] ...

        This script just employs Leiningen to run a Figwheel REPL server.
        It is run from a terminal command line to compile and start the Owlet
        ClojureScript app and run the Figwheel auto-compilation and REPL
        environment in the terminal. By default, a clean rebuild is performed,
        and rlwrap is used to provide readline editing at the REPL.

        If the --dirac option is provided, Figwheel will still run Owlet and
        reload modified code, but instead of the Figwheel REPL in the terminal,
        a Dirac server is started instead. Now just click the Dirac DevTools
        icon to the right of the address bar in Chrome Canary to get the REPL
        window. See https://github.com/binaryage/dirac

        All this works using a Clojure nREPL on port 8230. You can connect to
        it with another client, say an IDE, to get a Clojure, not ClojureScript
        REPL. To then obtain a ClojureScript REPL from within that Clojure
        REPL, evaluate (figwheel-sidecar.repl-api/cljs-repl). If you provided
        the --dirac option, instead evaluate (dirac! :join)."

maybe_rlwrap='rlwrap'
maybe_clean='clean,'
profile='+figwheel'

for opt in $@; do
    case ${opt#--} in
      no-readline)
        maybe_rlwrap=''
        ;;
      no-clean)
        maybe_clean=''
        ;;
      dirac)
        profile='+dirac'
        ;;
      *)
        echo "$USAGE"
        exit 0
        ;;
    esac
done

echo "$maybe_rlwrap lein do $maybe_clean with-profile $profile repl"
$maybe_rlwrap lein do $maybe_clean with-profile $profile repl

