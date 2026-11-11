CREATE TABLE agenda_preference
(
    campaign_id          UUID    NOT NULL,
    account_id           UUID    NOT NULL,
    presence             TEXT,
    digital_gaming_table BOOLEAN NOT NULL,

    CONSTRAINT pk_agenda_preference PRIMARY KEY (campaign_id, account_id),
    CONSTRAINT fk_agenda_preference_campaign FOREIGN KEY (campaign_id) REFERENCES campaign (id) ON DELETE CASCADE
);
