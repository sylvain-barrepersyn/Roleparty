package app.roleparty.campaign.infrastructure.adapter.primary

import app.roleparty.campaign.contract.web.CampaignOverviewResponseBody
import app.roleparty.campaign.contract.web.CampaignResponseBody
import app.roleparty.campaign.contract.web.InvitationResponseBody
import app.roleparty.campaign.domain.model.Campaign
import app.roleparty.campaign.domain.model.CampaignOverview
import app.roleparty.campaign.domain.model.Invitation

internal fun CampaignOverview.toResponseBody() = CampaignOverviewResponseBody(id, name, image, players.size, nextSession)

internal fun Campaign.toResponseBody() = CampaignResponseBody(id, name, description, image, gameMaster, players.sortedBy { it.toString() })

internal fun Invitation.toResponseBody() = InvitationResponseBody(code.toString(), used)
