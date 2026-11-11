package app.roleparty.campaign.contract.web

import app.roleparty.campaign.commons.CampaignId
import io.ktor.resources.Resource
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
@Resource("/v1/campaigns")
class CampaignsResource(val participation: Participation? = null, val today: LocalDate? = null) {

    @Serializable
    @Resource("join")
    class Join(val parent: CampaignsResource = CampaignsResource())

    @Serializable
    @Resource("{campaignId}")
    class Campaign(val campaignId: CampaignId, val parent: CampaignsResource = CampaignsResource()) {

        @Serializable
        @Resource("invitations")
        class Invitations(val parent: Campaign)
    }
}
