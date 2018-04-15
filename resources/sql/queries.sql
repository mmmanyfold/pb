-- :name create-voter! :! :n
-- :doc creates a new voter record
INSERT INTO voters
(id, first_name, last_name, email, pass)
VALUES (:id, :first_name, :last_name, :email, :pass)

-- :name update-voter! :! :n
-- :doc updates an existing voter record
UPDATE voters
SET first_name = :first_name, last_name = :last_name, email = :email
WHERE id = :id

-- :name get-voter :? :1
-- :doc retrieves a voter record given the id
SELECT * FROM voters
WHERE id = :id

-- :name delete-voter! :! :n
-- :doc deletes a voter record given the id
DELETE FROM voters
WHERE id = :id
