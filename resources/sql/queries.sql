-- :name create-voter! :! :n
-- :doc creates a new voter record
INSERT INTO voters
(additional_id, phone, admin, is_active, code, election, campus)
VALUES (:additional_id, :phone, :admin, :is_active, :code, :election, :campus)

-- :name create-voter-without-code-returning-id! :<! :1
-- :doc creates a new voter record
INSERT INTO voters
(additional_id, admin, is_active, election, campus)
VALUES (:additional_id, :admin, :is_active, :election, :campus)
RETURNING id

-- :name get-voter-by-phone :? :1
-- :doc retrieves a voter record given the phone number and election
SELECT * FROM voters
WHERE phone = :phone AND election = :election

-- :name get-voter-by-code :? :1
-- :doc retrives a voter record given the voter code and election
SELECT * FROM voters
WHERE code LIKE :code AND election = :election

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
(vote, election)
VALUES (:vote, :election)
RETURNING *

-- :name get-votes :? :1
-- :doc retrieves all votes for an election
SELECT * FROM votes
WHERE election = :election

-- :name create-voter-vote! :! :n
-- :doc creates new voter-vote record
INSERT INTO voter_votes
(voter_id, vote_id, election)
VALUES (:voter_id, :vote_id, :election)

-- :name get-voter-vote :? :1
-- :doc gets voter-vote given the voter id
SELECT * FROM voter_votes
WHERE voter_id = :id

-- :name get-votes :? :*
-- :doc gets votes in election
SELECT vote FROM votes;
