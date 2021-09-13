# sep3-tools
Tools for processing SEP 3 geological data.

## Components
The SEP3-Tools are based on [ANTLR](https://www.antlr.org/) and are providing a tool to parse coded strings such as `^u(t,lw)`. The grammar of these codes is defined in file [PetroGrammar](https://github.com/lat-lon/sep3-tools/blob/main/src/main/antlr4/org/sep3tools/gen/PetroGrammar.g4) and translated into a parser using the [Java](https://www.java.com) programming language. 

## License
SEP3-Tools are distributed under the GNU Lesser General Public License, Version 2.1 (LGPL 2.1). More information about the license can be found [here](https://github.com/lat-lon/sep3-tools/blob/main/LICENSE). 

## Requirements

### Woerterbuch data

Download dictionary ("Woerterbuch") data from https://www.lbeg.niedersachsen.de/karten_daten_publikationen/bohrdatenbank/sep_3/softwaredownloads/software-downloads-875.html as follows:

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

Test your conversion by e.g. retrieving all "Woerterbuch" table entries for the "PETRO" data field:

```
sep3=# select "Langtext" as "Typ", "Kuerzel", "Klartext" from "Woerterbuch" w join "Schluesseltypen" s on w."Typ" = s."Nebentypbez" where s."Datenfeld" = 'PETRO' order by "Typ", "Kuerzel";
     Typ      |                    Typbezeichnung                     | Kuerzel |                     Klartext                     
--------------+-------------------------------------------------------+---------+--------------------------------------------------
 Ergaenz_Allg | Allgemeine Ergänzungsattribute (Eigenschaften)        | afg     | aufgearbeitet
 Ergaenz_Allg | Allgemeine Ergänzungsattribute (Eigenschaften)        | agl     | aufgelockert
 Ergaenz_Allg | Allgemeine Ergänzungsattribute (Eigenschaften)        | agw     | ausgewaschen
 Ergaenz_Allg | Allgemeine Ergänzungsattribute (Eigenschaften)        | al      | allochthon
[...]
 Spetro_Oz    | Zersetzungsgrad nach SCHNEEKLOTH (1977)               | zg3     | mäßig zersetzt
 Spetro_Oz    | Zersetzungsgrad nach SCHNEEKLOTH (1977)               | zg4     | stark zersetzt
 Spetro_Oz    | Zersetzungsgrad nach SCHNEEKLOTH (1977)               | zg5     | sehr stark zersetzt
 Spetro_Oz    | Zersetzungsgrad nach SCHNEEKLOTH (1977)               | zgu     | unzersetzt
(2113 rows)
```

## Installation

## Hacking
To build SEP3-Tools you need to install a [JDK 16](https://adoptium.net/?variant=openjdk16&jvmVariant=hotspot) and [Apache Maven](https://maven.apache.org/).
Then run the following command:

```
mvn clean install
```

## Contact

Please send bug reports to the github repository.
https://github.com/lat-lon/sep3-tools/issues
