package app.roleparty.campaign.library.infrastructure.adapter.secondary

import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.database.JournalEntryImageTable
import app.roleparty.campaign.database.JournalEntryTable
import app.roleparty.campaign.library.commons.JournalEntryId
import app.roleparty.campaign.library.domain.model.JournalEntry
import app.roleparty.campaign.library.domain.port.secondary.JournalEntries
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.batchInsert
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.upsert

internal class InfrastructuredJournalEntries(private val database: R2dbcDatabase) : JournalEntries {

    override suspend fun save(journalEntry: JournalEntry) {
        suspendTransaction(database) {
            JournalEntryTable.upsert {
                it[id] = journalEntry.id
                it[playerCharacterId] = journalEntry.playerCharacterId
                it[title] = journalEntry.content.title
                it[text] = journalEntry.content.text
            }
            JournalEntryImageTable.deleteWhere { JournalEntryImageTable.journalEntryId eq journalEntry.id }
            JournalEntryImageTable.batchInsert(journalEntry.content.images.withIndex()) { image ->
                this[JournalEntryImageTable.journalEntryId] = journalEntry.id
                this[JournalEntryImageTable.position] = image.index
                this[JournalEntryImageTable.imageId] = image.value
            }
        }
    }

    override suspend fun findById(journalEntryId: JournalEntryId) = findWhere { JournalEntryTable.id eq journalEntryId }.firstOrNull()

    override suspend fun ofPlayerCharacter(playerCharacterId: PlayerCharacterId) = findWhere { JournalEntryTable.playerCharacterId eq playerCharacterId }

    private suspend fun findWhere(condition: () -> Op<Boolean>): List<JournalEntry> =
        suspendTransaction(database) {
            val rows = JournalEntryTable.selectAll().where(condition()).orderBy(JournalEntryTable.id, SortOrder.ASC).toList()
            val images = JournalEntryImageTable.select(JournalEntryImageTable.journalEntryId, JournalEntryImageTable.imageId)
                .where { JournalEntryImageTable.journalEntryId inList rows.map { it[JournalEntryTable.id] } }
                .orderBy(JournalEntryImageTable.position, SortOrder.ASC)
                .map { it[JournalEntryImageTable.journalEntryId] to it[JournalEntryImageTable.imageId] }
                .toList()
                .groupBy({ it.first }, { it.second })
            rows.map {
                val id = it[JournalEntryTable.id]
                JournalEntry(
                    id = id,
                    playerCharacterId = it[JournalEntryTable.playerCharacterId],
                    content = JournalEntry.Content(it[JournalEntryTable.title], it[JournalEntryTable.text], images[id].orEmpty()),
                )
            }
        }
}
