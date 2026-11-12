CREATE TABLE tabletop
(
    campaign_id     UUID      NOT NULL,
    game_session_id UUID      NOT NULL,
    started_at      TIMESTAMP NOT NULL,

    CONSTRAINT pk_tabletop PRIMARY KEY (campaign_id),
    CONSTRAINT fk_tabletop_campaign FOREIGN KEY (campaign_id) REFERENCES campaign (id) ON DELETE CASCADE,
    CONSTRAINT fk_tabletop_game_session FOREIGN KEY (game_session_id) REFERENCES game_session (id) ON DELETE CASCADE
);
