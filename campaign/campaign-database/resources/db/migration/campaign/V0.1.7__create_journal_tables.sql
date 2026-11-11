CREATE TABLE journal_entry
(
    id                  UUID NOT NULL,
    player_character_id UUID NOT NULL,
    title               TEXT NOT NULL,
    text                TEXT NOT NULL,

    CONSTRAINT pk_journal_entry PRIMARY KEY (id),
    CONSTRAINT fk_journal_entry_player_character FOREIGN KEY (player_character_id) REFERENCES player_character (id) ON DELETE CASCADE
);

CREATE INDEX idx_journal_entry_player_character ON journal_entry (player_character_id);

CREATE TABLE journal_entry_image
(
    journal_entry_id UUID    NOT NULL,
    position         INTEGER NOT NULL,
    image_id         UUID    NOT NULL,

    CONSTRAINT pk_journal_entry_image PRIMARY KEY (journal_entry_id, position),
    CONSTRAINT fk_journal_entry_image_journal_entry FOREIGN KEY (journal_entry_id) REFERENCES journal_entry (id) ON DELETE CASCADE
);
