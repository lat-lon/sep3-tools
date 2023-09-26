#!/bin/sh

set -e

# CONSTANTS for setting environment variables used by psql client
PGUSER=postgres
DATABASE=postgres

#SET client_min_messages TO DEBUG;
#SET pljava.libjvm_location TO '/usr/lib/jvm/java-11-openjdk-amd64/lib/server/libjvm.so';
#SET pljava.module_path TO '/usr/share/postgresql/12/pljava/';
#LOAD '/usr/lib/postgresql/12/lib/libpljava-so-1.6.4.so';

psql -q -c "CREATE EXTENSION pljava;"
#psql -q -p $PORT -U $POSTGRES_USER -c "SET pljava.libjvm_location TO '/usr/lib/jvm/java-11-openjdk-amd64/lib/server/libjvm.so';"
#psql -q -p $PORT -U $POSTGRES_USER -c "ALTER database postgres SET pljava.libjvm_location FROM current;"
psql -q -c "SELECT sqlj.install_jar('file:///opt/sep3-tools/sep3-parser.jar', 'sep3', true);"
psql -q -c "SELECT sqlj.set_classpath('public', 'sep3');"