package app.roleparty.campaign.story.infrastructure.adapter.secondary

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.database.LibraryEntryTable
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.story.domain.port.secondary.LibraryEntries
import kotlinx.coroutines.flow.firstOrNull
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

internal class InfrastructuredLibraryEntries(private val database: R2dbcDatabase) : LibraryEntries {

    override suspend fun campaignOf(libraryEntryId: LibraryEntryId): CampaignId? =
        suspendTransaction(database) {
            LibraryEntryTable.select(LibraryEntryTable.campaignId).where { LibraryEntryTable.id eq libraryEntryId }.firstOrNull()?.get(LibraryEntryTable.campaignId)
        }
}
