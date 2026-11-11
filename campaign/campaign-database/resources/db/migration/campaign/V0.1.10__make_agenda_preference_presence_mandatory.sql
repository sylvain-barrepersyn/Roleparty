UPDATE agenda_preference preference
SET presence = CASE WHEN campaign.game_master = preference.account_id THEN 'IN_PERSON_WITH_COMPUTER' ELSE 'ABSENT' END
FROM campaign
WHERE campaign.id = preference.campaign_id
  AND preference.presence IS NULL;

ALTER TABLE agenda_preference ALTER COLUMN presence SET NOT NULL;
