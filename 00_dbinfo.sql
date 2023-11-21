--
-- db info
--
CREATE table public.sep3tools (
  part VARCHAR (100),
  value VARCHAR (255)
);

COPY public.sep3tools (part, value)
FROM 'db-1.properties'
DELIMITER '='
QUOTE '@'
CSV
;

