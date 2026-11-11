package app.roleparty.campaign.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.domain.model.Campaign
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.campaign.domain.model.CampaignOverview
import app.roleparty.core.commons.Outcome
import kotlinx.datetime.LocalDate

interface FindCampaigns {

    suspend fun masteredBy(accountId: AccountId, today: LocalDate): List<CampaignOverview>

    suspend fun playedBy(accountId: AccountId, today: LocalDate): List<CampaignOverview>

    suspend fun byId(requester: AccountId, campaignId: CampaignId): Outcome<Campaign, CampaignError>
}
