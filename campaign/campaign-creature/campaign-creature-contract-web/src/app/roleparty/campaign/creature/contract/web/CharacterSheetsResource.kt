package app.roleparty.campaign.creature.contract.web

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.CharacterSheetId
import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/v1/campaigns/{campaignId}/character-sheets")
class CharacterSheetsResource(val campaignId: CampaignId) {

    @Serializable
    @Resource("{characterSheetId}")
    class CharacterSheet(val parent: CharacterSheetsResource, val characterSheetId: CharacterSheetId) {

        @Serializable
        @Resource("unlink")
        class Unlink(val parent: CharacterSheet)
    }
}
