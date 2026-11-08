package app.roleparty.campaign.library.infrastructure.adapter.secondary

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.database.LibraryEntryTable
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.library.domain.model.LibraryEntry
import app.roleparty.campaign.library.domain.port.secondary.LibraryEntries
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.upsert

internal class InfrastructuredLibraryEntries(private val database: R2dbcDatabase) : LibraryEntries {

    override suspend fun save(libraryEntry: LibraryEntry) {
        suspendTransaction(database) {
            LibraryEntryTable.upsert {
                it[id] = libraryEntry.id
                it[campaignId] = libraryEntry.campaignId
                it[title] = libraryEntry.metadata.title
                it[description] = libraryEntry.metadata.description
                it[imageId] = libraryEntry.image
            }
        }
    }

    override suspend fun findById(libraryEntryId: LibraryEntryId) = findWhere { LibraryEntryTable.id eq libraryEntryId }.firstOrNull()

    override suspend fun ofCampaign(campaignId: CampaignId) = findWhere { LibraryEntryTable.campaignId eq campaignId }

    private suspend fun findWhere(condition: () -> Op<Boolean>): List<LibraryEntry> =
        suspendTransaction(database) {
            LibraryEntryTable.selectAll().where(condition()).orderBy(LibraryEntryTable.id, SortOrder.ASC).toList().map { it.libraryEntry() }
        }

    private fun ResultRow.libraryEntry() = LibraryEntry(
        id = this[LibraryEntryTable.id],
        campaignId = this[LibraryEntryTable.campaignId],
        metadata = LibraryEntry.Metadata(this[LibraryEntryTable.title], this[LibraryEntryTable.description]),
        image = this[LibraryEntryTable.imageId],
    )
}
