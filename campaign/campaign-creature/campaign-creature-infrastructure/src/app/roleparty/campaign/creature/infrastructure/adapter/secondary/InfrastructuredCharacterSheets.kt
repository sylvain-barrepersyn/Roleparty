package app.roleparty.campaign.creature.infrastructure.adapter.secondary

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.CharacterSheetId
import app.roleparty.campaign.creature.domain.model.CharacterSheet
import app.roleparty.campaign.creature.domain.model.CharacterStats
import app.roleparty.campaign.creature.domain.port.secondary.CharacterSheets
import app.roleparty.campaign.database.CharacterSheetTable
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

internal class InfrastructuredCharacterSheets(private val database: R2dbcDatabase) : CharacterSheets {

    override suspend fun save(characterSheet: CharacterSheet) {
        val own = (characterSheet.stats as? CharacterStats.Own)?.stats
        suspendTransaction(database) {
            CharacterSheetTable.upsert {
                it[id] = characterSheet.id
                it[campaignId] = characterSheet.campaignId
                it[name] = characterSheet.name
                it[imageId] = characterSheet.image
                it[notes] = characterSheet.notes
                it[monsterSheetId] = (characterSheet.stats as? CharacterStats.Inherited)?.monsterSheetId
                it[size] = own?.size?.name
                it[type] = own?.type?.name
                it[challengeRating] = own?.challengeRating?.name
                it[content] = own?.content()
            }
        }
    }

    override suspend fun findById(characterSheetId: CharacterSheetId): CharacterSheet? =
        suspendTransaction(database) {
            CharacterSheetTable.selectAll().where { CharacterSheetTable.id eq characterSheetId }.firstOrNull()?.characterSheet()
        }

    override suspend fun ofCampaign(campaignId: CampaignId): List<CharacterSheet> =
        suspendTransaction(database) {
            CharacterSheetTable.selectAll()
                .where { CharacterSheetTable.campaignId eq campaignId }
                .orderBy(CharacterSheetTable.id, SortOrder.ASC)
                .map { it.characterSheet() }
                .toList()
        }

    private fun ResultRow.characterSheet() = CharacterSheet(
        id = this[CharacterSheetTable.id],
        campaignId = this[CharacterSheetTable.campaignId],
        name = this[CharacterSheetTable.name],
        image = this[CharacterSheetTable.imageId],
        notes = this[CharacterSheetTable.notes],
        stats = this[CharacterSheetTable.monsterSheetId]?.let { CharacterStats.Inherited(it) } ?: CharacterStats.Own(
            statBlockOf(
                checkNotNull(this[CharacterSheetTable.size]),
                checkNotNull(this[CharacterSheetTable.type]),
                checkNotNull(this[CharacterSheetTable.challengeRating]),
                checkNotNull(this[CharacterSheetTable.content]),
            ),
        ),
    )
}
