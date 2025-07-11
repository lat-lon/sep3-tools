# sep3-tools
Tools for processing SEP 3 geological data.

## Usage

SEP3-Tools currently supports the following data field values:	

* BESCHBG - Beschreibung Bohrgut
* BESCHBV - Beschreibung Bohrvorgangsbeschreibung
* BGRUPPE - Bodengruppe
* FARBE
* GENESE
* KALKGEH - Kalkgehalt
* PETRO - Petrographie
* ZUSATZ

These can be used in the following functions as *data_field* parameter values:

* text **s3_asbmllitho** (text *input_value*, text schluesselmapping_table)
* text **s3_astext** (text *input_value*)
* text **s3_astext** (text *input_value*, text *data_field*)
* text **s3_astext** (text *input_value*, text *woerterbuch_table*, text *schluesseltypen_table*, text *data_field*)
* text **s3_astext_verbose** (text *input_value*, text *woerterbuch_table*, text *schluesseltypen_table*, text *data_field*)

Some example function calls along with the results:

```
sep3=# SELECT s3_asbmllitho('^hzk, fS(ms2, "gl"2)', 'bml.bml_schluesselmapping');
 s3_asbmllitho 
---------------
 fS,mS


sep3=# SELECT s3_astext('^s(kgm-kgg,hf,F:hgn=gr');
                            s3_astext                             
------------------------------------------------------------------
 Sandsteinmittelkörnig bis grobkörnig, hartfest, hellgrünlichgrau
  

sep3=# SELECT s3_astext('(gG-mG-fG)(gs-fs)', 'PETRO');
                                               s3_astext                                                
--------------------------------------------------------------------------------------------------------
 (Grobkies [20-63 mm] bis Mittelkies [6,3-20 mm] bis Feinkies [2,0-6,3 mm]) (grobsandig bis feinsandig)


sep3=# SELECT s3_astext('((robn,rovi,bnvi,gngr,hbngr)(wl))(ob),rovi,robn,rovibn,bnro,(gn,hge,ro,gngr,holgr)(lag)', 'FARBE');
                                                                                            s3_astext                                                                                            
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 ((rotbraun, rotviolett, braunviolett, grüngrau, hellbraungrau) (wechsellagernd)) (oben), rotviolett, rotbraun, rotviolettbraun, braunrot, (grün, hellgelb, rot, grüngrau, hellolivgrau) (Lagen)


sep3=# SELECT s3_astext('(P:t4)(Mg,lw),gf,g', 'woerterbuch."Woerterbuch"', 'woerterbuch."Schluesseltypen"', 'GENESE');
                              s3_astext                               
----------------------------------------------------------------------
 (stark tonig) (Geschiebemergel, lagenweise), glazifluviatil, glaziär


sep3=# SELECT s3_astext('kf,k(lse,F:we)', 'woerterbuch."Woerterbuch"', 'woerterbuch."Schluesseltypen"', 'KALKGEH');
                s3_astext                
-----------------------------------------
 kalkfrei, kalkhaltig (als Linse, weiss)


sep3=# SELECT s3_astext_verbose('^s(kgm-kgg,hf,F:hgn=gr', 'woerterbuch."Woerterbuch"', 'woerterbuch."Schluesseltypen"', 'PETRO');
                        s3_astext_verbose                         
------------------------------------------------------------------
 Sandsteinmittelkörnig bis grobkörnig, hartfest, hellgrünlichgrau
```

## Components
The SEP3-Tools are based on [ANTLR](https://www.antlr.org/) and are providing a tool to parse coded strings such as `^u(t,lw)`. The grammar of these codes is defined in file [PetroGrammar](https://github.com/lat-lon/sep3-tools/blob/main/src/main/antlr4/org/sep3tools/gen/PetroGrammar.g4) and translated into a parser using the [Java](https://www.java.com) programming language. 

## License
SEP3-Tools are distributed under the GNU Lesser General Public License, Version 2.1 (LGPL 2.1). More information about the license can be found [here](https://github.com/lat-lon/sep3-tools/blob/main/LICENSE). 

## Contact
Please send bug reports to the github repository.
https://github.com/lat-lon/sep3-tools/issues

## Requirements

Install a [PostgreSQL](https://www.postgresql.org/) database 12+.

### Woerterbuch data

Download the dictionary ("Woerterbuch") data from [www.lbeg.niedersachsen.de](https://www.lbeg.niedersachsen.de/karten_daten_publikationen/bohrdatenbank/sep_3/softwaredownloads/software-downloads-875.html) as follows:

- "Schlüssellisten mit Kürzeln und zugehörigem Klartext und Typisierungen" - "Wörterbuch" - "August 2023"

It's a ZIP file which contains the `accdb` and `mdb` files.

Convert to PostgreSQL, e.g. using [mdbtools](https://github.com/mdbtools/mdbtools):

```
$ mdb-schema -T Schluesseltypen Woerterbuch_Austausch_Internet_accdb.accdb postgres > Schluesseltypen_create-table.sql
$ mdb-export Woerterbuch_Austausch_Internet_accdb.accdb Schluesseltypen > Schluesseltypen.csv
$ mdb-schema -T Woerterbuch Woerterbuch_Austausch_Internet_accdb.accdb postgres > Woerterbuch_create-table.sql
$ mdb-export -D %F Woerterbuch_Austausch_Internet_accdb.accdb Woerterbuch > Woerterbuch.csv
```

Note that starting from version 1.0.0 MDB Tools creates lower case table and columns names. The respective configuration file is db-1.properties whereas db-2.properties maps to tables and columns with upper case initial letters.

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

Test your conversion by e.g. retrieving all _"Woerterbuch"_ table entries for the _"PETRO"_ data field. Here, table and column names with upper case initial letters are used:

```
sep3=# select "Typ", "Langtext" as "Typbezeichnung", "Kuerzel", "Klartext" from woerterbuch."Woerterbuch" w join woerterbuch."Schluesseltypen" s on w."Typ" = s."Nebentypbez" where s."Datenfeld" = 'PETRO' order by "Typ", "Kuerzel";
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

To build SEP3-Tools you need to install a [JDK 11](https://adoptium.net/?variant=openjdk11&jvmVariant=hotspot) and [Apache Maven 3.9.x](https://maven.apache.org/).
Then run the following command to build the parser:

```shell
mvn clean install
```
_**NOTE:**_ Downloading dependencies from GitHub Repositories require a `GITHUB_TOKEN` set in your local Maven `settings.xml` file, see [Working with the Apache Maven registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry) for more information.

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
Schluffstein (tonig, lagenweise)
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

Create the configuration table `sep3tools` which maps the _"Schluesseltypen"_ and _"Woerterbuch"_ tables to the tool by using 00_dbinfo.sql and one of the two properties files db-1.properties or db-2.properties. db-1.properties uses lower case table and column names whereas db-2.properties has upper case initial letters:

```postgres-sql
sep3=# \i /tmp/00_dbinfo.sql
```

Verify the installation by executing the function `S3_AsText()`:
```postgres-sql
SELECT S3_AsText('gr');
```
... which should translate to "granitisch" whereas

```postgres-sql
SELECT S3_AsText('gr', 'FARBE');
```
... which should translate to "grau".

The example shows that "PETRO" is used as default value.


You can get the description of the available functions by executing the `\df` command:

```postgres-sql
petroparser=# \df *s3_*
                                                   List of functions
 Schema |       Name        | Result data type  |                                     Argument data types                                      | Type 
--------+-------------------+-------------------+----------------------------------------------------------------------------------------------+------
 public | s3_asbmllitho     | character varying | s3string character varying, sm character varying                                             | func
 public | s3_astext         | character varying | s3string character varying                                                                   | func
 public | s3_astext         | character varying | s3string character varying, df character varying                                             | func
 public | s3_astext         | character varying | s3string character varying, wb character varying, st character varying, df character varying | func
 public | s3_astext_verbose | character varying | s3string character varying, wb character varying, st character varying, df character varying | func
(5 rows)
```

## Docker

SEP3-Tools can be used with Docker. Follow the instructions to execute commands on a docker environment:

### Pull image

Pull the latest image from github:

```shell
docker pull ghcr.io/lat-lon/sep3-tools:latest
```
Replace `latest` with an existing version to pull the docker image of a specific version.

### Using

Start the container with
```shell
docker run --name sep3tools --rm -e POSTGRES_PASSWORD=postgres ghcr.io/lat-lon/sep3-tools:latest
```

Login to the container
```shell
docker exec -it sep3tools bash
```

Execute commands on the container

```shell
root@9e9294939f62:/docker-entrypoint-initdb.d# psql -h localhost -Upostgres -d sep3tools

sep3tools=# \df *s3_*;
List of functions
Schema |       Name        | Result data type  |                                     Argument data types                                      | Type
--------+-------------------+-------------------+----------------------------------------------------------------------------------------------+------
public | s3_asbmllitho     | character varying | s3string character varying, sm character varying                                             | func
public | s3_astext         | character varying | s3string character varying                                                                   | func
public | s3_astext         | character varying | s3string character varying, df character varying                                             | func
public | s3_astext         | character varying | s3string character varying, wb character varying, st character varying, df character varying | func
public | s3_astext_verbose | character varying | s3string character varying, wb character varying, st character varying, df character varying | func
(5 rows)
```

### Building

To build SEP3-Tools docker image on your own you need to install a [JDK 11](https://adoptium.net/?variant=openjdk11&jvmVariant=hotspot) and [Apache Maven 3.8.x](https://maven.apache.org/).
Then run the following command to build the parser:

```shell
mvn clean install
```

Build the docker image with
```shell
docker build -t sep3tools .
```