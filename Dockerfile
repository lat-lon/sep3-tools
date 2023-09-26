# This file is available under the following license:
# under LGPL 2.1 (LICENSE.TXT) Copyright 2023 Torsten Friebe <tfr@lat-lon.de>
ARG POSTGRES_VERSION=14
ARG POSTGIS_VERSION=3.4
ARG JDK_VERSION=11

#FROM tfr42/postgis:${POSTGRES_VERSION}-${POSTGIS_VERSION}
FROM postgis/postgis:${POSTGRES_VERSION}-${POSTGIS_VERSION}
LABEL maintainer="lat/lon GmbH <info@lat-lon.de"

RUN apt-get update -y && apt-get install --no-install-recommends -y postgresql-14-pljava openjdk-11-jdk mdbtools

COPY ./target/sep3-parser-*-jar-with-dependencies.jar /opt/sep3-tools/sep3-parser.jar
COPY ./install_pljava.sh /docker-entrypoint-initdb.d/install_pljava.sh