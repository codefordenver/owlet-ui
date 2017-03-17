#!/usr/bin/env bash

READY_SCRIPT=~/.owlet_repo_ready
if test -f $READY_SCRIPT; then
    $READY_SCRIPT
fi
