package app.roleparty.campaign.agenda.contract.web

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.commons.CampaignId
import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/v1/campaigns/{campaignId}/game-sessions")
class GameSessionsResource(val campaignId: CampaignId) {

    @Serializable
    @Resource("{gameSessionId}/name")
    class Name(val parent: GameSessionsResource, val gameSessionId: GameSessionId)

    @Serializable
    @Resource("{gameSessionId}/presences/{accountId}")
    class Presence(val parent: GameSessionsResource, val gameSessionId: GameSessionId, val accountId: AccountId)

    @Serializable
    @Resource("{gameSessionId}/digital-gaming-table")
    class DigitalGamingTable(val parent: GameSessionsResource, val gameSessionId: GameSessionId)
}
