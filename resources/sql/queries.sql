-- :name create-voter! :! :n
-- :doc creates a new voter record
INSERT INTO voters
(phone, admin, is_active, code)
VALUES (:phone, :admin, :is_active, :code)

-- :name update-voter! :! :n
-- :doc updates an existing voter record
UPDATE voters
SET first_name = :first_name, last_name = :last_name, email = :email
WHERE id = :id

-- :name get-voter-by-phone :? :1
-- :doc retrieves a voter record given the id
SELECT * FROM voters
WHERE phone = :phone

-- :name get-voter-by-code :? :1
-- :doc retrives a voter record given their voter code
SELECT * FROM voters
WHERE code LIKE :code

-- :name delete-voter! :! :n
-- :doc deletes a voter record given the id
DELETE FROM voters
WHERE id = :id
