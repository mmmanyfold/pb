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
-- :doc retrives a voter record given the voter code
SELECT * FROM voters
WHERE code LIKE :code

-- :name get-voter-by-id :? :1
-- :doc retrieves a voter record given the id
SELECT * FROM voters
WHERE id = :id

-- :name delete-voter! :! :n
-- :doc deletes a voter record given the id
DELETE FROM voters
WHERE id = :id

-- :name create-vote! :<! :1
-- :doc creates a new vote record
INSERT INTO votes
(vote)
VALUES (:vote)
RETURNING *

-- :name get-votes :? :1
-- :doc retrieves all votes
SELECT * FROM votes

-- :name create-voter-vote! :! :n
-- :doc creates new voter-vote record
INSERT INTO voter_votes
(voter_id, vote_id)
VALUES (:voter_id, :vote_id)

-- :name get-voter-vote :? :1
-- :doc gets voter-vote given the voter id
SELECT * FROM voter_votes
WHERE voter_id = :id
