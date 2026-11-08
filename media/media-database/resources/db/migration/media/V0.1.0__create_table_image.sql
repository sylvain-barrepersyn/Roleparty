CREATE TABLE image
(
    id      UUID  NOT NULL,
    content BYTEA NOT NULL,
    format  TEXT  NOT NULL,

    CONSTRAINT pk_image PRIMARY KEY (id)
);
