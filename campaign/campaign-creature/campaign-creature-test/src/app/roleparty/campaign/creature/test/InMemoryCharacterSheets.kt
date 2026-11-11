package app.roleparty.campaign.creature.test

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.CharacterSheetId
import app.roleparty.campaign.creature.domain.model.CharacterSheet
import app.roleparty.campaign.creature.domain.port.secondary.CharacterSheets

internal class InMemoryCharacterSheets : CharacterSheets {

    private val characterSheets = linkedMapOf<CharacterSheetId, CharacterSheet>()

    override suspend fun save(characterSheet: CharacterSheet) {
        characterSheets[characterSheet.id] = characterSheet
    }

    override suspend fun findById(characterSheetId: CharacterSheetId) = characterSheets[characterSheetId]

    override suspend fun ofCampaign(campaignId: CampaignId) = characterSheets.values.filter { it.campaignId == campaignId }
}
