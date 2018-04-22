CREATE TABLE voters
(id SERIAL PRIMARY KEY,
 phone VARCHAR(15),
 admin BOOLEAN,
 is_active BOOLEAN,
 code VARCHAR(64));