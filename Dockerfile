FROM postgres:12

ENV POSTGRES_VERSION 12
ENV JAVA_VERSION 17

ARG WBFILENAME=/tmp/Woerterbuch_Austausch_Internet_accdb.accdb

ENV POSTGRES_USER petroparser
ENV POSTGRES_PASSWORD petroparser
ENV POSTGRES_DB petroparser

RUN apt update
RUN apt install -y \
  mdbtools \
  wget \
  unzip \
  postgresql-$POSTGRES_VERSION-pljava

RUN wget -O /tmp/sep3examples.zip https://www.lbeg.niedersachsen.de/download/49897
RUN unzip /tmp/sep3examples.zip -d /tmp

RUN mdb-schema -T Schluesseltypen $WBFILENAME postgres > /tmp/Schluesseltypen_create-table.sql
RUN mdb-export $WBFILENAME Schluesseltypen > /tmp/Schluesseltypen.csv
RUN mdb-schema -T Woerterbuch $WBFILENAME postgres > /tmp/Woerterbuch_create-table.sql
RUN mdb-export -D %F $WBFILENAME Woerterbuch > /tmp/Woerterbuch.csv

COPY ./target/sep3-parser-0.0.1-SNAPSHOT-jar-with-dependencies.jar /tmp/

COPY ./src/main/resources/*.properties /tmp/propfiles/

RUN for file in /tmp/propfiles/*.properties; do \
    iconv -f iso-8859-1 -t utf8 -o "$file.new" "$file" && mv -f "$file.new" "$file"; \
done

COPY ./src/main/resources/*.sql /docker-entrypoint-initdb.d/
