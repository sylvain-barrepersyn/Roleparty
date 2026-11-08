package app.roleparty.campaign.infrastructure.adapter.secondary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.database.CampaignTable
import app.roleparty.campaign.database.InvitationTable
import app.roleparty.campaign.database.PlayerTable
import app.roleparty.campaign.domain.model.Campaign
import app.roleparty.campaign.domain.model.CampaignOverview
import app.roleparty.campaign.domain.model.Invitation
import app.roleparty.campaign.domain.port.secondary.Campaigns
import app.roleparty.campaign.event.CampaignCreated
import app.roleparty.campaign.event.PlayerJoinedCampaign
import app.roleparty.core.outbox.enqueue
import kotlin.time.Clock
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.inSubQuery
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.R2dbcTransaction
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update

internal class InfrastructuredCampaigns(
    private val database: R2dbcDatabase,
    private val clock: Clock,
) : Campaigns {

    override suspend fun create(campaign: Campaign) {
        suspendTransaction(database) {
            CampaignTable.insert {
                it[id] = campaign.id
                it[name] = campaign.name
                it[description] = campaign.description
                it[imageId] = campaign.image
                it[gameMaster] = campaign.gameMaster
            }
            enqueue(CampaignCreated(campaign.id, campaign.gameMaster), clock.now())
        }
    }

    override suspend fun findById(campaignId: CampaignId): Campaign? =
        suspendTransaction(database) {
            CampaignTable.selectAll().where { CampaignTable.id eq campaignId }.firstOrNull()?.let {
                Campaign(
                    id = it[CampaignTable.id],
                    name = it[CampaignTable.name],
                    description = it[CampaignTable.description],
                    image = it[CampaignTable.imageId],
                    gameMaster = it[CampaignTable.gameMaster],
                    players = playersOf(listOf(campaignId))[campaignId].orEmpty(),
                )
            }
        }

    override suspend fun masteredBy(accountId: AccountId) = overviews { CampaignTable.gameMaster eq accountId }

    override suspend fun playedBy(accountId: AccountId) = overviews {
        CampaignTable.id inSubQuery PlayerTable.select(PlayerTable.campaignId).where { PlayerTable.accountId eq accountId }
    }

    override suspend fun addPlayer(campaignId: CampaignId, player: AccountId, redeemed: Invitation.Code): Boolean =
        suspendTransaction(database) {
            val redeemedCount = InvitationTable.update({
                (InvitationTable.code eq redeemed.toString()) and (InvitationTable.campaignId eq campaignId) and (InvitationTable.used eq false)
            }) { it[used] = true }
            if (redeemedCount == 0) return@suspendTransaction false

            PlayerTable.insert {
                it[PlayerTable.campaignId] = campaignId
                it[accountId] = player
            }
            enqueue(PlayerJoinedCampaign(campaignId, player), clock.now())
            true
        }

    private suspend fun overviews(condition: () -> Op<Boolean>): List<CampaignOverview> =
        suspendTransaction(database) {
            val rows = CampaignTable.selectAll().where(condition()).orderBy(CampaignTable.id, SortOrder.ASC).toList()
            val players = playersOf(rows.map { it[CampaignTable.id] })
            rows.map { it.overview(players[it[CampaignTable.id]].orEmpty()) }
        }

    private suspend fun R2dbcTransaction.playersOf(campaignIds: List<CampaignId>): Map<CampaignId, Set<AccountId>> =
        PlayerTable.selectAll().where { PlayerTable.campaignId inList campaignIds }
            .map { it[PlayerTable.campaignId] to it[PlayerTable.accountId] }
            .toList()
            .groupBy({ it.first }, { it.second })
            .mapValues { it.value.toSet() }

    private fun ResultRow.overview(players: Set<AccountId>) =
        CampaignOverview(this[CampaignTable.id], this[CampaignTable.name], this[CampaignTable.imageId], this[CampaignTable.gameMaster], players)
}
