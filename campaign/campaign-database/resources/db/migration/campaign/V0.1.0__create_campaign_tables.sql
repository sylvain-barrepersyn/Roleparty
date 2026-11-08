CREATE TABLE campaign
(
    id          UUID NOT NULL,
    name        TEXT NOT NULL,
    description TEXT NOT NULL,
    image_id    UUID NOT NULL,
    game_master UUID NOT NULL,

    CONSTRAINT pk_campaign PRIMARY KEY (id)
);

CREATE INDEX idx_campaign_game_master ON campaign (game_master);

CREATE TABLE player
(
    campaign_id UUID NOT NULL,
    account_id  UUID NOT NULL,

    CONSTRAINT pk_player PRIMARY KEY (campaign_id, account_id),
    CONSTRAINT fk_player_campaign FOREIGN KEY (campaign_id) REFERENCES campaign (id) ON DELETE CASCADE
);

CREATE INDEX idx_player_account ON player (account_id);

CREATE TABLE invitation
(
    code        TEXT    NOT NULL,
    campaign_id UUID    NOT NULL,
    used        BOOLEAN NOT NULL,

    CONSTRAINT pk_invitation PRIMARY KEY (code),
    CONSTRAINT fk_invitation_campaign FOREIGN KEY (campaign_id) REFERENCES campaign (id) ON DELETE CASCADE
);

CREATE INDEX idx_invitation_campaign ON invitation (campaign_id);

CREATE TABLE player_character
(
    id                  UUID    NOT NULL,
    campaign_id         UUID    NOT NULL,
    player              UUID    NOT NULL,
    name                TEXT    NOT NULL,
    image_id            UUID    NOT NULL,
    alignment           TEXT    NOT NULL,
    strength            INTEGER NOT NULL,
    dexterity           INTEGER NOT NULL,
    constitution        INTEGER NOT NULL,
    intelligence        INTEGER NOT NULL,
    wisdom              INTEGER NOT NULL,
    charisma            INTEGER NOT NULL,
    species             TEXT    NOT NULL,
    species_size        TEXT,
    background          TEXT    NOT NULL,
    background_tool     TEXT,
    appearance          TEXT    NOT NULL,
    backstory           TEXT    NOT NULL,
    skill_proficiencies TEXT    NOT NULL,
    languages           TEXT    NOT NULL,

    CONSTRAINT pk_player_character PRIMARY KEY (id),
    CONSTRAINT fk_player_character_campaign FOREIGN KEY (campaign_id) REFERENCES campaign (id) ON DELETE CASCADE
);

CREATE INDEX idx_player_character_campaign_player ON player_character (campaign_id, player);

CREATE TABLE player_character_class
(
    player_character_id UUID    NOT NULL,
    character_class     TEXT    NOT NULL,
    level               INTEGER NOT NULL,
    position            INTEGER NOT NULL,

    CONSTRAINT pk_player_character_class PRIMARY KEY (player_character_id, character_class),
    CONSTRAINT fk_player_character_class_player_character FOREIGN KEY (player_character_id) REFERENCES player_character (id) ON DELETE CASCADE
);
