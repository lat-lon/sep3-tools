--
-- beschbgtest
--
CREATE table test (
  input VARCHAR (100),
  expected VARCHAR (255)
);

COPY test (input, expected)
FROM '/tmp/propfiles/beschbgtest.properties'
DELIMITER '='
CSV
;

SELECT input, expected, s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','BESCHBG') AS output, 'failed' AS beschbgtest FROM test
  WHERE expected != s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','BESCHBG')
UNION
SELECT input, expected, s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','BESCHBG') AS output, 'success' AS beschbgtest FROM test 
  WHERE expected = s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','BESCHBG')
;

DROP TABLE  test;

--
-- beschbvtest
--
CREATE table test (
  input VARCHAR (100),
  expected VARCHAR (255)
);

COPY test (input, expected)
FROM '/tmp/propfiles/beschbvtest.properties'
DELIMITER '='
CSV
;

SELECT input, expected, s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','BESCHBV') AS output, 'failed' AS beschbvtest FROM test
  WHERE expected != s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','BESCHBV')
UNION
SELECT input, expected, s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','BESCHBV') AS output, 'success' AS beschbvtest FROM test 
  WHERE expected = s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','BESCHBV')
;

DROP TABLE  test;

--
--  bgruppetest
--
CREATE table test (
  input VARCHAR (100),
  expected VARCHAR (255)
);

COPY test (input, expected)
FROM '/tmp/propfiles/bgruppetest.properties'
DELIMITER '='
CSV
;

SELECT input, expected, s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','BGRUPPE') AS output, 'failed' AS bgruppetest FROM test
  WHERE expected != s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','BGRUPPE')
UNION
SELECT input, expected, s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','BGRUPPE') AS output, 'success' AS bgruppetest FROM test
  where expected = s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','BGRUPPE')
;

DROP TABLE  test;

--
-- farbetest
--
CREATE table test (
  input VARCHAR (100),
  expected VARCHAR (255)
);

COPY test (input, expected)
FROM '/tmp/propfiles/farbetest.properties'
DELIMITER '='
CSV
;

SELECT input, expected, s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','FARBE') AS output, 'failed' AS farbetest FROM test
  WHERE expected != s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','FARBE')
UNION
SELECT input, expected, s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','FARBE') AS output, 'success' AS farbetest FROM test
  where expected = s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','FARBE')
;

DROP TABLE  test;

--
-- genesetest
--
CREATE table test (
  input VARCHAR (100),
  expected VARCHAR (255)
);

COPY test (input, expected)
FROM '/tmp/propfiles/genesetest.properties'
DELIMITER '='
CSV
;

SELECT input, expected, s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','GENESE') AS output, 'failed' AS genesetest FROM test
  WHERE expected != s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','GENESE')
UNION
SELECT input, expected, s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','GENESE') AS output, 'success' AS genesetest FROM test
  where expected = s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','GENESE')
;

DROP TABLE  test;

--
-- kalkgehtest
--
CREATE table test (
  input VARCHAR (100),
  expected VARCHAR (255)
);

COPY test (input, expected)
FROM '/tmp/propfiles/kalkgehtest.properties'
DELIMITER '='
CSV
;

SELECT input, expected, s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','KALKGEH') AS output, 'failed' AS kalkgehtest FROM test
  WHERE expected != s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','KALKGEH')
UNION
SELECT input, expected, s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','KALKGEH') AS output, 'success' AS kalkgehtest FROM test
  where expected = s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','KALKGEH')
;

DROP TABLE  test;

--
-- petrotest
--
CREATE table test (
  input VARCHAR (255),
  expected VARCHAR (511)
);

COPY test (input, expected)
FROM '/tmp/propfiles/petrotest.properties' WITH
DELIMITER '='
QUOTE '@'
CSV
;

SELECT input, expected, s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','PETRO') AS output, 'failed' AS petrotest FROM test
  WHERE expected != s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','PETRO')
UNION
SELECT input, expected, s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','PETRO') AS output, 'success' AS petrotest FROM test
  where expected = s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','PETRO')
;

DROP TABLE  test;

--
--zusatztest
--
CREATE table test (
  input VARCHAR (100),
  expected VARCHAR (255)
);

COPY test (input, expected)
FROM '/tmp/propfiles/zusatztest.properties' WITH
DELIMITER '='
QUOTE '@'
CSV
;

SELECT input, expected, s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','ZUSATZ') AS output, 'failed' AS zusatztest FROM test
  WHERE expected != s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','ZUSATZ')
UNION
SELECT input, expected, s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','ZUSATZ') AS output, 'success' AS zusatztest FROM test
  where expected = s3_astext(input,'woerterbuch.woerterbuch','woerterbuch.schluesseltypen','ZUSATZ')
;

DROP TABLE  test;

