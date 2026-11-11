ALTER TABLE game_session ADD COLUMN digital_gaming_table BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE game_session_presence
(
    game_session_id UUID NOT NULL,
    account_id      UUID NOT NULL,
    presence        TEXT NOT NULL,

    CONSTRAINT pk_game_session_presence PRIMARY KEY (game_session_id, account_id),
    CONSTRAINT fk_game_session_presence_game_session FOREIGN KEY (game_session_id) REFERENCES game_session (id) ON DELETE CASCADE
);
