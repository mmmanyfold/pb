CREATE TABLE voters
(id SERIAL PRIMARY KEY,
 additional_id VARCHAR,
 phone VARCHAR(15),
 admin BOOLEAN,
 is_active BOOLEAN,
 code VARCHAR(110),
 election VARCHAR);
