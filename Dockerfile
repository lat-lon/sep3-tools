# This file is available under the following license:
# under LGPL 2.1 (LICENSE.TXT) Copyright 2023 Torsten Friebe <info@lat-lon.de>
ARG POSTGRES_VERSION=14
ARG POSTGIS_VERSION=3.4

FROM postgis/postgis:${POSTGRES_VERSION}-${POSTGIS_VERSION}
LABEL maintainer="lat/lon GmbH <info@lat-lon.de" \
    "org.opencontainers.image.description"="sep3 tools" \
	"org.opencontainers.image.licenses"="GNU Lesser General Public License & others" \
	"org.opencontainers.image.url"="https://github.com/lat-lon/sep3-tools" \
	"org.opencontainers.image.vendor"="lat/lon GmbH"

ARG POSTGRES_VERSION=14
ARG JDK_VERSION=11

ENV WBFILENAME=/tmp/Woerterbuch_Austausch_Internet_accdb.accdb

RUN apt-get update -y && \
   apt-get install --no-install-recommends -y postgresql-${POSTGRES_VERSION}-pljava openjdk-${JDK_VERSION}-jdk mdbtools wget unzip
WORKDIR /docker-entrypoint-initdb.d

ENV DATA_DIR=/docker-entrypoint-initdb.d

RUN wget -O /tmp/sep3examples.zip https://www.lbeg.niedersachsen.de/download/49897
RUN unzip /tmp/sep3examples.zip -d /tmp

COPY ./target/sep3-parser-*-jar-with-dependencies.jar /opt/sep3-tools/sep3-parser.jar
COPY ./install_pljava.sh /docker-entrypoint-initdb.d/install_pljava.sh
COPY ./importData.sql /tmp/importData.sql

RUN mdb-schema -T Schluesseltypen $WBFILENAME postgres > /tmp/Schluesseltypen_create-table.sql
RUN mdb-export $WBFILENAME Schluesseltypen > /tmp/Schluesseltypen.csv
RUN mdb-schema -T Woerterbuch $WBFILENAME postgres > /tmp/Woerterbuch_create-table.sql
RUN mdb-export -D %F $WBFILENAME Woerterbuch > /tmp/Woerterbuch.csv