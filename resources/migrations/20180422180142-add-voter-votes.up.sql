CREATE TABLE voter_votes
(voter_id int REFERENCES voters (id) ON UPDATE CASCADE ON DELETE CASCADE,
 vote_id int REFERENCES votes (id) ON UPDATE CASCADE,
 election VARCHAR,
 -- explicit primary key
 CONSTRAINT votervotes_pk PRIMARY KEY (voter_id, vote_id));
