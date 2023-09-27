create schema IF NOT EXISTS woerterbuch;
set search_path = woerterbuch, public;
\i Schluesseltypen_create-table.sql;
\copy Schluesseltypen from 'Schluesseltypen.csv' CSV HEADER;
\i Woerterbuch_create-table.sql;
set datestyle to 'SQL,MDY';
\copy Woerterbuch from 'Woerterbuch.csv' CSV HEADER;