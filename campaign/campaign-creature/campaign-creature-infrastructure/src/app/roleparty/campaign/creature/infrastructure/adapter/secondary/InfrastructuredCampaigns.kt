package app.roleparty.campaign.creature.infrastructure.adapter.secondary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.domain.port.secondary.Campaigns
import app.roleparty.campaign.database.CampaignTable
import app.roleparty.campaign.database.PlayerTable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet
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

    override suspend fun playersOf(campaignId: CampaignId): Set<AccountId> =
        suspendTransaction(database) {
            PlayerTable.select(PlayerTable.accountId).where { PlayerTable.campaignId eq campaignId }.map { it[PlayerTable.accountId] }.toSet()
        }
}
