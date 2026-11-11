package app.roleparty.campaign.agenda.infrastructure.adapter.secondary

import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.agenda.domain.model.GameSession
import app.roleparty.campaign.agenda.domain.model.Presence
import app.roleparty.campaign.agenda.domain.port.secondary.GameSessions
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.database.GameSessionPresenceTable
import app.roleparty.campaign.database.GameSessionTable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.batchInsert
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.upsert

internal class InfrastructuredGameSessions(private val database: R2dbcDatabase) : GameSessions {

    override suspend fun save(gameSession: GameSession) {
        suspendTransaction(database) {
            GameSessionTable.upsert {
                it[id] = gameSession.id
                it[campaignId] = gameSession.campaignId
                it[date] = gameSession.date
                it[name] = gameSession.name
                it[digitalGamingTable] = gameSession.digitalGamingTable
            }
            GameSessionPresenceTable.deleteWhere { GameSessionPresenceTable.gameSessionId eq gameSession.id }
            GameSessionPresenceTable.batchInsert(gameSession.presences.entries) { entry ->
                this[GameSessionPresenceTable.gameSessionId] = gameSession.id
                this[GameSessionPresenceTable.accountId] = entry.key
                this[GameSessionPresenceTable.presence] = entry.value.name
            }
        }
    }

    override suspend fun findById(gameSessionId: GameSessionId): GameSession? = findWhere { GameSessionTable.id eq gameSessionId }.firstOrNull()

    override suspend fun ofCampaign(campaignId: CampaignId): List<GameSession> = findWhere { GameSessionTable.campaignId eq campaignId }

    private suspend fun findWhere(condition: () -> Op<Boolean>): List<GameSession> =
        suspendTransaction(database) {
            val rows = GameSessionTable.selectAll().where(condition())
                .orderBy(GameSessionTable.date to SortOrder.ASC, GameSessionTable.id to SortOrder.ASC)
                .toList()
            val presences = GameSessionPresenceTable.selectAll()
                .where { GameSessionPresenceTable.gameSessionId inList rows.map { it[GameSessionTable.id] } }
                .map { it[GameSessionPresenceTable.gameSessionId] to (it[GameSessionPresenceTable.accountId] to Presence.valueOf(it[GameSessionPresenceTable.presence])) }
                .toList()
                .groupBy({ it.first }, { it.second })
            rows.map {
                val id = it[GameSessionTable.id]
                GameSession(
                    id = id,
                    campaignId = it[GameSessionTable.campaignId],
                    date = it[GameSessionTable.date],
                    name = it[GameSessionTable.name],
                    digitalGamingTable = it[GameSessionTable.digitalGamingTable],
                    presences = presences[id].orEmpty().toMap(),
                )
            }
        }
}
