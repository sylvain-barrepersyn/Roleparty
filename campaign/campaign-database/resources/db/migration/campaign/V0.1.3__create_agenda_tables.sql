CREATE TABLE game_session
(
    id          UUID NOT NULL,
    campaign_id UUID NOT NULL,
    date        DATE NOT NULL,
    name        TEXT,

    CONSTRAINT pk_game_session PRIMARY KEY (id),
    CONSTRAINT fk_game_session_campaign FOREIGN KEY (campaign_id) REFERENCES campaign (id) ON DELETE CASCADE
);

CREATE INDEX idx_game_session_campaign ON game_session (campaign_id, date);
