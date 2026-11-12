package app.roleparty.campaign.tabletop.contract.web

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/v1/campaigns/{campaignId}/tabletop")
class TabletopResource(val campaignId: CampaignId) {

    @Serializable
    @Resource("events")
    class Events(val parent: TabletopResource)

    @Serializable
    @Resource("seat")
    class Seat(val parent: TabletopResource, val playerCharacterId: PlayerCharacterId? = null) {

        @Serializable
        @Resource("signals")
        class Signals(val parent: Seat)
    }
}
