package app.roleparty.campaign.agenda.infrastructure.adapter.secondary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.domain.port.secondary.PlayerCharacters
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.database.PlayerCharacterTable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

internal class InfrastructuredPlayerCharacters(private val database: R2dbcDatabase) : PlayerCharacters {

    override suspend fun namesByPlayer(campaignId: CampaignId): Map<AccountId, List<String>> =
        suspendTransaction(database) {
            PlayerCharacterTable.select(PlayerCharacterTable.player, PlayerCharacterTable.name)
                .where { PlayerCharacterTable.campaignId eq campaignId }
                .orderBy(PlayerCharacterTable.id, SortOrder.ASC)
                .map { it[PlayerCharacterTable.player] to it[PlayerCharacterTable.name] }
                .toList()
                .groupBy({ it.first }, { it.second })
        }
}
