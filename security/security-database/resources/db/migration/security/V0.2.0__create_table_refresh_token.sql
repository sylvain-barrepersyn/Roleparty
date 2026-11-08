CREATE TABLE refresh_token
(
    id         UUID PRIMARY KEY,
    account_id UUID      NOT NULL,
    expiration TIMESTAMP NOT NULL,
    revoked    BOOLEAN   NOT NULL,
    creation   TIMESTAMP NOT NULL
);

CREATE INDEX idx_refresh_token_account_id ON refresh_token (account_id);
