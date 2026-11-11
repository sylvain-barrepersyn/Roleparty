package app.roleparty.campaign.agenda.contract.web

import app.roleparty.campaign.commons.CampaignId
import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/v1/campaigns/{campaignId}/agenda-preference")
class AgendaPreferenceResource(val campaignId: CampaignId)
