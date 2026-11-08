CREATE TABLE monster_sheet
(
    id               UUID  NOT NULL,
    campaign_id      UUID  NOT NULL,
    name             TEXT  NOT NULL,
    image_id         UUID,
    size             TEXT  NOT NULL,
    type             TEXT  NOT NULL,
    challenge_rating TEXT  NOT NULL,
    content          JSONB NOT NULL,

    CONSTRAINT pk_monster_sheet PRIMARY KEY (id),
    CONSTRAINT fk_monster_sheet_campaign FOREIGN KEY (campaign_id) REFERENCES campaign (id) ON DELETE CASCADE
);

CREATE INDEX idx_monster_sheet_campaign ON monster_sheet (campaign_id);
