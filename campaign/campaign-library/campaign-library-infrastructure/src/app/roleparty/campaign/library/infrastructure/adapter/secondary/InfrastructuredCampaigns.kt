package app.roleparty.campaign.library.infrastructure.adapter.secondary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.database.CampaignTable
import app.roleparty.campaign.library.domain.port.secondary.Campaigns
import kotlinx.coroutines.flow.firstOrNull
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

internal class InfrastructuredCampaigns(private val database: R2dbcDatabase) : Campaigns {

    override suspend fun gameMasterOf(campaignId: CampaignId): AccountId? =
        suspendTransaction(database) {
            CampaignTable.select(CampaignTable.gameMaster).where { CampaignTable.id eq campaignId }.firstOrNull()?.get(CampaignTable.gameMaster)
        }
}
