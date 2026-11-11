package app.roleparty.campaign.infrastructure.adapter.secondary

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.database.GameSessionTable
import app.roleparty.campaign.domain.port.secondary.GameSessions
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.min
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

internal class InfrastructuredGameSessions(private val database: R2dbcDatabase) : GameSessions {

    override suspend fun nextDates(campaignIds: Collection<CampaignId>, from: LocalDate): Map<CampaignId, LocalDate> {
        if (campaignIds.isEmpty()) return emptyMap()
        val nextDate = GameSessionTable.date.min()
        return suspendTransaction(database) {
            GameSessionTable.select(GameSessionTable.campaignId, nextDate)
                .where { (GameSessionTable.campaignId inList campaignIds) and (GameSessionTable.date greaterEq from) }
                .groupBy(GameSessionTable.campaignId)
                .map { it[GameSessionTable.campaignId] to it[nextDate]!! }
                .toList()
                .toMap()
        }
    }
}
