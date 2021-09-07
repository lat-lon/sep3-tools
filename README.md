# sep3-tools
Tools for processing SEP 3 geological data.

## Components

## License

## Requirements

### Woerterbuch data

Download Woerterbuch data from https://www.lbeg.niedersachsen.de/karten_daten_publikationen/bohrdatenbank/sep_3/softwaredownloads/software-downloads-875.html as follows:

- "Schlüssellisten mit Kürzeln und zugehörigem Klartext und Typisierungen" - "Wörterbuch" - "Juli 2021"

Convert to PostgreSQL, e.g. using mdbtools (https://github.com/mdbtools/mdbtools):

```
$ mdb-schema -T Schluesseltypen Woerterbuch_Austausch_Internet_accdb.accdb postgres > Schluesseltypen_create-table.sql
$ mdb-export Woerterbuch_Austausch_Internet_accdb.accdb Schluesseltypen > Schluesseltypen.csv
$ mdb-schema -T Woerterbuch Woerterbuch_Austausch_Internet_accdb.accdb postgres > Woerterbuch_create-table.sql
$ mdb-export -D %F Woerterbuch_Austausch_Internet_accdb.accdb Woerterbuch > Woerterbuch.csv
```

Create the "Schluesseltypen" and "Woerterbuch" tables in your PostgreSQL database, e.g. in their own "woerterbuch" schema:

```
sep3=# create schema woerterbuch;
sep3=# set search_path = woerterbuch, public;
sep3=# \i /tmp/Schluesseltypen_create-table.sql
sep3=# \copy "Schluesseltypen" from '/tmp/Schluesseltypen.csv' CSV HEADER
sep3=# \i /tmp/Woerterbuch_create-table.sql
sep3=# \copy "Woerterbuch" from '/tmp/Woerterbuch.csv' CSV HEADER
```

## Installation

## Hacking

## Contact

Please send bug reports to the github repository.
https://github.com/lat-lon/sep3-tools/issues
