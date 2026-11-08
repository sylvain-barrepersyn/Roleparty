package app.roleparty.campaign.agenda.infrastructure.adapter.secondary

import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.database.GameSessionTable
import app.roleparty.campaign.agenda.domain.model.GameSession
import app.roleparty.campaign.agenda.domain.port.secondary.GameSessions
import app.roleparty.campaign.commons.CampaignId
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
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
            }
        }
    }

    override suspend fun findById(gameSessionId: GameSessionId): GameSession? =
        suspendTransaction(database) { GameSessionTable.selectAll().where { GameSessionTable.id eq gameSessionId }.firstOrNull()?.gameSession() }

    override suspend fun ofCampaign(campaignId: CampaignId): List<GameSession> =
        suspendTransaction(database) {
            GameSessionTable.selectAll().where { GameSessionTable.campaignId eq campaignId }
                .orderBy(GameSessionTable.date to SortOrder.ASC, GameSessionTable.id to SortOrder.ASC)
                .map { it.gameSession() }
                .toList()
        }

    private fun ResultRow.gameSession() =
        GameSession(this[GameSessionTable.id], this[GameSessionTable.campaignId], this[GameSessionTable.date], this[GameSessionTable.name])
}
