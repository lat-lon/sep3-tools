#!/bin/sh

set -e

# CONSTANTS for setting environment variables used by psql client
PGUSER=postgres
DATABASE=postgres

# debug settings
#SET client_min_messages TO DEBUG;

psql -q -c "CREATE database sep3tools;"

# installs pljava extension
psql -q -d sep3tools -c "CREATE EXTENSION pljava;"
psql -q -d sep3tools -c "SELECT sqlj.install_jar('file:///opt/sep3-tools/sep3-parser.jar', 'sep3', true);"
psql -q -d sep3tools -c "SELECT sqlj.set_classpath('public', 'sep3');"

psql -q -d sep3tools -f /tmp/importData.sql