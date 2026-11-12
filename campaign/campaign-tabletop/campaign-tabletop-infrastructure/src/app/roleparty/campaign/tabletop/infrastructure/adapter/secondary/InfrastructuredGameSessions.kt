package app.roleparty.campaign.tabletop.infrastructure.adapter.secondary

import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.database.GameSessionTable
import app.roleparty.campaign.tabletop.domain.model.GameSession
import app.roleparty.campaign.tabletop.domain.port.secondary.GameSessions
import kotlinx.coroutines.flow.firstOrNull
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

internal class InfrastructuredGameSessions(private val database: R2dbcDatabase) : GameSessions {

    override suspend fun findById(gameSessionId: GameSessionId): GameSession? =
        suspendTransaction(database) {
            GameSessionTable.select(GameSessionTable.id, GameSessionTable.campaignId, GameSessionTable.date, GameSessionTable.name)
                .where { GameSessionTable.id eq gameSessionId }
                .firstOrNull()
                ?.let { GameSession(it[GameSessionTable.id], it[GameSessionTable.campaignId], it[GameSessionTable.date], it[GameSessionTable.name]) }
        }
}
