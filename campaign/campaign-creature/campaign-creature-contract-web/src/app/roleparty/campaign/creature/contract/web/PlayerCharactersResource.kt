package app.roleparty.campaign.creature.contract.web

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/v1/campaigns/{campaignId}/player-characters")
class CampaignPlayerCharactersResource(val campaignId: CampaignId)

@Serializable
@Resource("/v1/player-characters")
class PlayerCharactersResource {

    @Serializable
    @Resource("{playerCharacterId}")
    class PlayerCharacter(val playerCharacterId: PlayerCharacterId, val parent: PlayerCharactersResource = PlayerCharactersResource())
}
