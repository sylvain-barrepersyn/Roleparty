package app.roleparty.campaign.library.infrastructure.adapter.secondary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.database.PlayerCharacterTable
import app.roleparty.campaign.library.domain.port.secondary.PlayerCharacters
import kotlinx.coroutines.flow.firstOrNull
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

internal class InfrastructuredPlayerCharacters(private val database: R2dbcDatabase) : PlayerCharacters {

    override suspend fun playerOf(playerCharacterId: PlayerCharacterId): AccountId? =
        suspendTransaction(database) {
            PlayerCharacterTable.select(PlayerCharacterTable.player).where { PlayerCharacterTable.id eq playerCharacterId }.firstOrNull()
                ?.get(PlayerCharacterTable.player)
        }
}
