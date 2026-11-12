package app.roleparty.campaign.tabletop.infrastructure.adapter.secondary

import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.database.PlayerCharacterTable
import app.roleparty.campaign.tabletop.domain.model.PlayerCharacter
import app.roleparty.campaign.tabletop.domain.port.secondary.PlayerCharacters
import kotlinx.coroutines.flow.firstOrNull
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

internal class InfrastructuredPlayerCharacters(private val database: R2dbcDatabase) : PlayerCharacters {

    override suspend fun findById(playerCharacterId: PlayerCharacterId): PlayerCharacter? =
        suspendTransaction(database) {
            PlayerCharacterTable
                .select(PlayerCharacterTable.id, PlayerCharacterTable.campaignId, PlayerCharacterTable.player, PlayerCharacterTable.name, PlayerCharacterTable.imageId)
                .where { PlayerCharacterTable.id eq playerCharacterId }
                .firstOrNull()
                ?.let {
                    PlayerCharacter(
                        id = it[PlayerCharacterTable.id],
                        campaignId = it[PlayerCharacterTable.campaignId],
                        player = it[PlayerCharacterTable.player],
                        name = it[PlayerCharacterTable.name],
                        imageId = it[PlayerCharacterTable.imageId],
                    )
                }
        }
}
