CREATE TABLE credential
(
    account_id    UUID PRIMARY KEY,
    email_address TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL
);
