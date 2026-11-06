DROP TABLE event;

ALTER TABLE overview RENAME COLUMN account_id TO id;
ALTER TABLE overview RENAME TO account;
