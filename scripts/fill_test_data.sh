#!/usr/bin/env bash
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PACKAGE="org.bitbrothers.shoppi.debug"
adb $* shell run-as $PACKAGE sqlite3 /data/data/$PACKAGE/databases/main < $SCRIPT_DIR/testdata.sql
