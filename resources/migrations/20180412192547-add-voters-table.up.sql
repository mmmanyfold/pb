CREATE TABLE voters
(id SERIAL PRIMARY KEY,
 phone VARCHAR(10),
 admin BOOLEAN,
 is_active BOOLEAN,
 code VARCHAR(64));