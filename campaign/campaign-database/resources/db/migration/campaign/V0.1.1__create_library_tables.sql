CREATE TABLE library_entry
(
    id          UUID NOT NULL,
    campaign_id UUID NOT NULL,
    title       TEXT NOT NULL,
    description TEXT NOT NULL,
    image_id    UUID NOT NULL,

    CONSTRAINT pk_library_entry PRIMARY KEY (id),
    CONSTRAINT fk_library_entry_campaign FOREIGN KEY (campaign_id) REFERENCES campaign (id) ON DELETE CASCADE
);

CREATE INDEX idx_library_entry_campaign ON library_entry (campaign_id);
