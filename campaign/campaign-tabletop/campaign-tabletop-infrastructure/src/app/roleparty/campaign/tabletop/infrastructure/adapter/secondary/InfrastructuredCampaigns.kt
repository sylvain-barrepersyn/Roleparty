package app.roleparty.campaign.tabletop.infrastructure.adapter.secondary

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.database.CampaignTable
import app.roleparty.campaign.database.PlayerTable
import app.roleparty.campaign.tabletop.domain.model.CampaignParticipants
import app.roleparty.campaign.tabletop.domain.port.secondary.Campaigns
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

internal class InfrastructuredCampaigns(private val database: R2dbcDatabase) : Campaigns {

    override suspend fun participantsOf(campaignId: CampaignId): CampaignParticipants? =
        suspendTransaction(database) {
            val gameMaster = CampaignTable.select(CampaignTable.gameMaster).where { CampaignTable.id eq campaignId }.firstOrNull()
                ?.get(CampaignTable.gameMaster) ?: return@suspendTransaction null
            val players = PlayerTable.select(PlayerTable.accountId).where { PlayerTable.campaignId eq campaignId }.map { it[PlayerTable.accountId] }.toSet()
            CampaignParticipants(gameMaster, players)
        }
}
