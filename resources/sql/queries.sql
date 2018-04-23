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

-- :name get-voter :? :1
-- :doc retrieves a voter record given the id
SELECT * FROM voters
WHERE phone = :phone

-- :name delete-voter! :! :n
-- :doc deletes a voter record given the id
DELETE FROM voters
WHERE id = :id
