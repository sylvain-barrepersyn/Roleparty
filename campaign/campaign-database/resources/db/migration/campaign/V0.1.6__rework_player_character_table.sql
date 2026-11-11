DROP TABLE player_character_class;
DROP TABLE player_character;

CREATE TABLE player_character
(
    id          UUID  NOT NULL,
    campaign_id UUID  NOT NULL,
    player      UUID  NOT NULL,
    name        TEXT  NOT NULL,
    image_id    UUID  NOT NULL,
    content     JSONB NOT NULL,

    CONSTRAINT pk_player_character PRIMARY KEY (id),
    CONSTRAINT fk_player_character_campaign FOREIGN KEY (campaign_id) REFERENCES campaign (id) ON DELETE CASCADE
);

CREATE INDEX idx_player_character_campaign_player ON player_character (campaign_id, player);
