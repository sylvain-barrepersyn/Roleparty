UPDATE credential SET email_address = lower(email_address);

DROP TABLE refresh_token;

CREATE TABLE refresh_token
(
    token_hash TEXT PRIMARY KEY,
    account_id UUID      NOT NULL,
    device_id  UUID      NOT NULL,
    expiration TIMESTAMP NOT NULL,
    rotated_at TIMESTAMP,
    revoked    BOOLEAN   NOT NULL
);

CREATE INDEX idx_refresh_token_account_id ON refresh_token (account_id, device_id);
CREATE INDEX idx_refresh_token_expiration ON refresh_token (expiration);

CREATE TABLE email_code
(
    code_hash     TEXT      NOT NULL,
    purpose       TEXT      NOT NULL,
    email_address TEXT      NOT NULL,
    password_hash TEXT,
    expiration    TIMESTAMP NOT NULL,
    PRIMARY KEY (code_hash, purpose, email_address)
);

CREATE INDEX idx_email_code_address ON email_code (email_address, purpose);
CREATE INDEX idx_email_code_expiration ON email_code (expiration);
