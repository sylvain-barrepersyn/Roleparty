package app.roleparty.campaign.tabletop.domain.model

import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.commons.CampaignId
import kotlin.time.Instant

data class Tabletop(val campaignId: CampaignId, val gameSessionId: GameSessionId, val startedAt: Instant)
