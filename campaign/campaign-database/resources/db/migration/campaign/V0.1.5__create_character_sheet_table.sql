CREATE TABLE character_sheet
(
    id               UUID  NOT NULL,
    campaign_id      UUID  NOT NULL,
    name             TEXT  NOT NULL,
    image_id         UUID,
    notes            TEXT  NOT NULL,
    monster_sheet_id UUID,
    size             TEXT,
    type             TEXT,
    challenge_rating TEXT,
    content          JSONB,

    CONSTRAINT pk_character_sheet PRIMARY KEY (id),
    CONSTRAINT fk_character_sheet_campaign FOREIGN KEY (campaign_id) REFERENCES campaign (id) ON DELETE CASCADE,
    CONSTRAINT fk_character_sheet_monster_sheet FOREIGN KEY (monster_sheet_id) REFERENCES monster_sheet (id),
    CONSTRAINT ck_character_sheet_stats CHECK (
        (monster_sheet_id IS NOT NULL AND size IS NULL AND type IS NULL AND challenge_rating IS NULL AND content IS NULL)
            OR (monster_sheet_id IS NULL AND size IS NOT NULL AND type IS NOT NULL AND challenge_rating IS NOT NULL AND content IS NOT NULL)
        )
);

CREATE INDEX idx_character_sheet_campaign ON character_sheet (campaign_id);
CREATE INDEX idx_character_sheet_monster_sheet ON character_sheet (monster_sheet_id);
