# This file is available under the following license:
# under LGPL 2.1 (LICENSE.TXT) Copyright 2023 Torsten Friebe <info@lat-lon.de>
ARG POSTGRES_VERSION=14
ARG POSTGIS_VERSION=3.4
ARG JDK_VERSION=11

FROM postgis/postgis:${POSTGRES_VERSION}-${POSTGIS_VERSION}
LABEL maintainer="lat/lon GmbH <info@lat-lon.de" \
    "org.opencontainers.image.description"="sep3 tools" \
	"org.opencontainers.image.licenses"="GNU Lesser General Public License & others" \
	"org.opencontainers.image.url"="https://github.com/lat-lon/sep3-tools" \
	"org.opencontainers.image.vendor"="lat/lon GmbH"

RUN apt-get update -y && apt-get install --no-install-recommends -y postgresql-14-pljava openjdk-11-jdk mdbtools
WORKDIR /docker-entrypoint-initdb.d
ENV DATA_DIR=/docker-entrypoint-initdb.d
COPY ./target/sep3-parser-*-jar-with-dependencies.jar /opt/sep3-tools/sep3-parser.jar
# WIP download with curl fails
COPY ./Woerterbuch_Austausch_Internet_accdb.accdb /docker-entrypoint-initdb.d/Woerterbuch_Austausch_Internet_accdb.accdb
COPY ./install_pljava.sh /docker-entrypoint-initdb.d/install_pljava.sh