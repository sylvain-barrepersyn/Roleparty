CREATE TABLE event
(
    id          UUID      NOT NULL UNIQUE,
    account_id  UUID      NOT NULL,
    version     INTEGER   NOT NULL,
    occurred_at TIMESTAMP NOT NULL,
    payload     JSONB     NOT NULL,

    CONSTRAINT pk_event PRIMARY KEY (account_id, version)
);

CREATE TABLE overview
(
    account_id    UUID,
    email_address TEXT NOT NULL UNIQUE,
    role          TEXT NOT NULL,

    CONSTRAINT pk_overview PRIMARY KEY (account_id)
);
