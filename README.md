# sep3-tools
Tools for processing SEP 3 geological data.

## Components
The SEP3-Tools are based on [ANTLR](https://www.antlr.org/) and are providing a tool to parse coded strings such as `^u(t,lw)`. The grammar of these codes is defined in file [PetroGrammar](https://github.com/lat-lon/sep3-tools/blob/main/src/main/antlr4/org/sep3tools/gen/PetroGrammar.g4) and translated into a parser using the [Java](https://www.java.com) programming language. 

## License
SEP3-Tools are distributed under the GNU Lesser General Public License, Version 2.1 (LGPL 2.1). More information about the license can be found [here](https://github.com/lat-lon/sep3-tools/blob/main/LICENSE). 

## Contact
Please send bug reports to the github repository.
https://github.com/lat-lon/sep3-tools/issues

## Requirements

Install a [PostgreSQL](https://www.postgresql.org/) database 10+.

### Woerterbuch data

Download dictionary ("Woerterbuch") data from https://www.lbeg.niedersachsen.de/karten_daten_publikationen/bohrdatenbank/sep_3/softwaredownloads/software-downloads-875.html as follows:

- "Schlüssellisten mit Kürzeln und zugehörigem Klartext und Typisierungen" - "Wörterbuch" - "Juli 2021"

It's a ZIP file which contains the `accdb` and `mdb` files.

Convert to PostgreSQL, e.g. using [mdbtools](https://github.com/mdbtools/mdbtools):

```
$ mdb-schema -T Schluesseltypen Woerterbuch_Austausch_Internet_accdb.accdb postgres > Schluesseltypen_create-table.sql
$ mdb-export Woerterbuch_Austausch_Internet_accdb.accdb Schluesseltypen > Schluesseltypen.csv
$ mdb-schema -T Woerterbuch Woerterbuch_Austausch_Internet_accdb.accdb postgres > Woerterbuch_create-table.sql
$ mdb-export -D %F Woerterbuch_Austausch_Internet_accdb.accdb Woerterbuch > Woerterbuch.csv
```

Create the _"Schluesseltypen"_ and _"Woerterbuch"_ tables in your PostgreSQL database, e.g. in their own _"woerterbuch"_ schema:

```
sep3=# create schema woerterbuch;
sep3=# set search_path = woerterbuch, public;
sep3=# \i /tmp/Schluesseltypen_create-table.sql
sep3=# \copy "Schluesseltypen" from '/tmp/Schluesseltypen.csv' CSV HEADER
sep3=# \i /tmp/Woerterbuch_create-table.sql
sep3=# set datestyle to 'SQL,MDY';
sep3=# \copy "Woerterbuch" from '/tmp/Woerterbuch.csv' CSV HEADER
```

Test your conversion by e.g. retrieving all _"Woerterbuch"_ table entries for the _"PETRO"_ data field:

```
sep3=# select "Typ", "Langtext" as "Typbezeichnung", "Kuerzel", "Klartext" from "Woerterbuch" w join "Schluesseltypen" s on w."Typ" = s."Nebentypbez" where s."Datenfeld" = 'PETRO' order by "Typ", "Kuerzel";
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

### Building
To build SEP3-Tools you need to install a [JDK 11](https://adoptium.net/?variant=openjdk11&jvmVariant=hotspot) and [Apache Maven 3.8.x](https://maven.apache.org/).
Then run the following command to build the parser:

```shell
mvn clean install
```

To execute the parser you have to download ANTLR and all other dependencies first with:

```shell
mvn dependency:copy-dependencies -DoutputDirectory=target
```

Then you can execute the parser with:

```shell
java -jar ./target/sep3-parser-0.0.1-SNAPSHOT.jar "<soil_codes>"
```
for example:

```shell
$> java -jar ./target/sep3-parser-0.0.1-SNAPSHOT.jar "^u(t,lw)"
2021-09-14 12:00:00 WARN  org.sep3tools.PetroVisitor:20 - Dictionary is not available, fallback to internal dictionary if possible.
Schluff (tonig, lagenweise)
```
or using the Woerterbuch data with the options `sep3-parser [jdbc-url] [username] [password] [dictionary] [keytypes] <soil_codes>`:

```shell
$> java -jar ./target/sep3-parser-0.0.1-SNAPSHOT-jar-with-dependencies.jar "jdbc:postgresql://localhost:5432/postgres" "postgres" "postgres" "woerterbuch.\"Woerterbuch\"" "woerterbuch.\"Schluesseltypen\"" "G(fg-gg,ms-gs,mats,mata,grs(tw)),fX-mX(mata),mS(fs,grs,fg-mg2,mx(voe))"
Kies [gerundet] (feinkiesig bis grobkiesig, mittelsandig bis grobsandig, Schwarzwaldmaterial, alpines Material, grusig (teilweise)), Feinsteinstücke [2,0-6,3 mm] bis Mittelsteinstücke [6,3-20 mm] (alpines Material), Mittelsand [0,2-0,63 mm] (feinsandig, grusig, feinkiesig bis mittelkiesig2, mittelsteinig (vereinzelt vorhanden))
```

### Running inside PostgreSQL

You can install SEP3-Tools into a [PostgreSQL](https://www.postgresql.org/) database and execute the parser via a database function. The SEP3-Tools are using
the [PL/Java](https://tada.github.io/pljava) library to execute the parser via SQL.  

#### Prepare PostgreSQL

Follow the [PL/Java installation guide](https://tada.github.io/pljava/install/install.html) and install a JDK 11 on the machine running the PostgreSQL database.

The installation steps for PostgreSQL 12 with OpenJDK 11 and PL/Java v1.6.4 on Ubuntu 20.04.3 LTS in a nutshell:
```shell
apt-get update && apt-get -yq install postgresql-server-dev-12 openjdk-11-jdk maven git gcc libssl-dev libkrb5-dev 
git clone https://github.com/tada/pljava.git
cd pljava
git checkout tags/V1_6_4
mvn install 
sudo java -jar pljava-packaging/target/pljava-pg12.jar
```

or install the latest PL/Java version on Debian using the `postgresql-12-pljava` package: 
```shell
apt-get update && apt-get install postgresql-12-pljava
```

### Install SEP3-Tools in PostgreSQL

Now PL/Java is installed and the SEP3-Tools library needs to be loaded into the database. Connect to the PostgreSQL database with
`psql -U postgres` and execute the following statements:
```postgres-sql
SET pljava.libjvm_location TO '/usr/lib/jvm/java-11-openjdk-amd64/lib/server/libjvm.so';
ALTER database postgres SET pljava.libjvm_location FROM current;
CREATE EXTENSION pljava;
SELECT sqlj.install_jar('file:///<PATH_TO_SEP3-TOOLS>/target/sep3-parser-0.0.1-SNAPSHOT-jar-with-dependencies.jar', 'sep3', true);
SELECT sqlj.set_classpath('public', 'sep3');
```

Verify the installation by executing the function `parseS3()`:
```postgres-sql
SELECT parseS3('^u');
```
You can get the description of the function by executing the `\df` command:

```postgres-sql
postgres=#  \df *parseS3*
                           List of functions
 Schema |  Name   | Result data type  |   Argument data types    | Type 
--------+---------+-------------------+--------------------------+------
 public | parses3 | character varying | s3code character varying | func
(1 row)
```
