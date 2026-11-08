CREATE TABLE scene
(
    id          UUID NOT NULL,
    campaign_id UUID NOT NULL,
    name        TEXT NOT NULL,
    description TEXT NOT NULL,

    CONSTRAINT pk_scene PRIMARY KEY (id),
    CONSTRAINT fk_scene_campaign FOREIGN KEY (campaign_id) REFERENCES campaign (id) ON DELETE CASCADE
);

CREATE INDEX idx_scene_campaign ON scene (campaign_id);

CREATE TABLE scene_library_entry
(
    scene_id         UUID      NOT NULL,
    library_entry_id UUID      NOT NULL,
    position         BIGSERIAL NOT NULL,

    CONSTRAINT pk_scene_library_entry PRIMARY KEY (scene_id, library_entry_id),
    CONSTRAINT fk_scene_library_entry_scene FOREIGN KEY (scene_id) REFERENCES scene (id) ON DELETE CASCADE,
    CONSTRAINT fk_scene_library_entry_library_entry FOREIGN KEY (library_entry_id) REFERENCES library_entry (id) ON DELETE CASCADE
);

CREATE INDEX idx_scene_library_entry_library_entry ON scene_library_entry (library_entry_id);
