# sep3-tools
Tools for processing SEP 3 geological data.

## Components

## License

## Requirements

### Woerterbuch data

Download Woerterbuch data from https://www.lbeg.niedersachsen.de/karten_daten_publikationen/bohrdatenbank/sep_3/softwaredownloads/software-downloads-875.html as follows:

- Schlüssellisten mit Kürzeln und zugehörigem Klartext und Typisierungen - Wörterbuch - Juli 2021

Convert to PostgreSQL, e.g. using mdbtools (https://github.com/mdbtools/mdbtools):

```
$ mdb-schema -T Woerterbuch Woerterbuch_Austausch_Internet_accdb.accdb postgres > Woerterbuch_create-table.sql
$ mdb-export -I postgres Woerterbuch_Austausch_Internet_accdb.accdb Woerterbuch > Woerterbuch_inserts.sql
```

Create the Woerterbuch table in your PostgreSQL database by running the two SQL scripts.

## Installation

## Hacking

## Contact

Please send bug reports to the github repository.
https://github.com/lat-lon/sep3-tools/issues
