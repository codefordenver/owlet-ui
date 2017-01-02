#!/usr/bin/env bash
#
#   This script just starts up the owlet-ui app in Chrome Canary for debugging
#   with Dirac in MacOS.
#

chrome='/Applications/Google Chrome Canary.app/Contents/MacOS/Google Chrome Canary'
page='http://localhost:4000/'
user_data_dir='.dirac-chrome-profile'

test -f "$chrome"  ||  {
    echo 'See https://github.com/binaryage/dirac-sample/blob/master/readme.md'
    echo 'Google Chrome Canary must be installed. It is available here:'
    echo 'https://www.google.com/chrome/browser/canary.html'
    exit 1
}

test -d "$user_data_dir"  ||  mkdir "$user_data_dir"

echo "Launching Chrome Canary for $page"  &&  (     \
    "$chrome"                                       \
      --remote-debugging-port=9222                  \
      --no-first-run                                \
      --user-data-dir="$user_data_dir"              \
      "$page"                                       \
      2>"$user_data_dir/cmd-line.log"               \
    ||                                              \
    cat .dirac-chrome-profile/cmd-line.log          \
) &

