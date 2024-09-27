#!/bin/sh

set -e

# CONSTANTS for setting environment variables used by psql client
PGUSER=postgres
DATABASE=postgres

# debug settings
#SET client_min_messages TO DEBUG;

# installs pljava extension
psql -q -c "CREATE EXTENSION pljava;"
psql -q -c "SELECT sqlj.install_jar('file:///opt/sep3-tools/sep3-parser.jar', 'sep3', true);"
psql -q -c "SELECT sqlj.set_classpath('public', 'sep3');"

# prepares dictionary
# WIP - curl fails due to TLS error
#curl --tls-max 1.3 --tlsv1.2 -fsvo Woerterbuch_Austausch_Internet_accdb.accdb https://www.lbeg.niedersachsen.de/download/49897

mdb-schema -T Schluesseltypen Woerterbuch_Austausch_Internet_accdb.accdb postgres > $DATA_DIR/Schluesseltypen_create-table.sql
mdb-export Woerterbuch_Austausch_Internet_accdb.accdb Schluesseltypen > $DATA_DIR/Schluesseltypen.csv
mdb-schema -T Woerterbuch Woerterbuch_Austausch_Internet_accdb.accdb postgres > $DATA_DIR/Woerterbuch_create-table.sql
mdb-export -D %F Woerterbuch_Austausch_Internet_accdb.accdb Woerterbuch > $DATA_DIR/Woerterbuch.csv

psql -q -f importData.sql