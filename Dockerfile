FROM postgres:12 AS sep3

ARG JAVA_VERSION=17
ARG WBFILENAME=/tmp/Woerterbuch_Austausch_Internet_accdb.accdb
ARG INCLUDEINDBTESTS=FALSE

ENV POSTGRES_USER petroparser
ENV POSTGRES_PASSWORD petroparser
ENV POSTGRES_DB petroparser
ENV POSTGRES_VERSION 12

RUN apt update
RUN apt install -y \
  mdbtools \
  wget \
  unzip \
  postgresql-${POSTGRES_VERSION}-pljava

RUN wget -O /tmp/sep3examples.zip https://www.lbeg.niedersachsen.de/download/49897
RUN unzip /tmp/sep3examples.zip -d /tmp

RUN mdb-schema -T Schluesseltypen $WBFILENAME postgres > /tmp/Schluesseltypen_create-table.sql
RUN mdb-export $WBFILENAME Schluesseltypen > /tmp/Schluesseltypen.csv
RUN mdb-schema -T Woerterbuch $WBFILENAME postgres > /tmp/Woerterbuch_create-table.sql
RUN mdb-export -D %F $WBFILENAME Woerterbuch > /tmp/Woerterbuch.csv

COPY ./target/sep3-parser-0.0.1-SNAPSHOT-jar-with-dependencies.jar /tmp/
COPY ./src/main/resources/*install*.sql /docker-entrypoint-initdb.d/

FROM sep3 as sep3-with-tests
COPY ./src/main/resources/*test*.sql /docker-entrypoint-initdb.d/
COPY ./src/main/resources/*.properties /tmp/propfiles/

RUN for file in /tmp/propfiles/*.properties; do \
    iconv -f iso-8859-1 -t utf8 -o "$file.new" "$file" && mv -f "$file.new" "$file"; \
done