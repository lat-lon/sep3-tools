create schema IF NOT EXISTS woerterbuch;
set search_path = woerterbuch, public;
\i /tmp/Schluesseltypen_create-table.sql;
\copy woerterbuch."Schluesseltypen" from '/tmp/Schluesseltypen.csv' CSV HEADER;
\i /tmp/Woerterbuch_create-table.sql;
set datestyle to 'SQL,MDY';
\copy woerterbuch."Woerterbuch" from '/tmp/Woerterbuch.csv' CSV HEADER;