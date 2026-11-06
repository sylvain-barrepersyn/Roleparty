CREATE TABLE event
(
    position     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id           UUID      NOT NULL UNIQUE,
    exchange     TEXT      NOT NULL,
    type         TEXT      NOT NULL,
    payload      JSONB     NOT NULL,
    occurred_at  TIMESTAMP NOT NULL,
    published_at TIMESTAMP
);

CREATE INDEX event_unpublished ON event (position) WHERE published_at IS NULL;
