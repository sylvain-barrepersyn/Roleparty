package app.roleparty.campaign.creature.domain.port.secondary

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.CharacterSheetId
import app.roleparty.campaign.creature.domain.model.CharacterSheet

interface CharacterSheets {

    suspend fun save(characterSheet: CharacterSheet)

    suspend fun findById(characterSheetId: CharacterSheetId): CharacterSheet?

    suspend fun ofCampaign(campaignId: CampaignId): List<CharacterSheet>
}
