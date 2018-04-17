CREATE TABLE voters
(id VARCHAR(20) PRIMARY KEY,
 first_name VARCHAR(30),
 last_name VARCHAR(30),
 phone VARCHAR(10),
 admin BOOLEAN,
 last_login TIMESTAMP,
 is_active BOOLEAN,
 pass VARCHAR(300));