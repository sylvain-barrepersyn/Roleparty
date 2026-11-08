package app.roleparty.campaign.library.domain

import app.roleparty.campaign.library.domain.port.primary.ChangeLibraryEntryMetadata
import app.roleparty.campaign.library.domain.port.primary.CreateLibraryEntry
import app.roleparty.campaign.library.domain.port.primary.FindLibraryEntries
import app.roleparty.campaign.library.domain.port.secondary.Campaigns
import app.roleparty.campaign.library.domain.port.secondary.LibraryEntries
import app.roleparty.campaign.library.domain.service.GameMasterCheckingService
import app.roleparty.campaign.library.domain.service.LibraryEntryCreatingService
import app.roleparty.campaign.library.domain.service.LibraryEntryFindingService
import app.roleparty.campaign.library.domain.service.LibraryEntryMetadataChangingService

class LibraryModule(libraryEntries: LibraryEntries, campaigns: Campaigns) {

    private val gameMaster = GameMasterCheckingService(campaigns)

    val createLibraryEntry: CreateLibraryEntry = LibraryEntryCreatingService(libraryEntries, gameMaster)

    val findLibraryEntries: FindLibraryEntries = LibraryEntryFindingService(libraryEntries, gameMaster)

    val changeLibraryEntryMetadata: ChangeLibraryEntryMetadata = LibraryEntryMetadataChangingService(libraryEntries, gameMaster)
}
