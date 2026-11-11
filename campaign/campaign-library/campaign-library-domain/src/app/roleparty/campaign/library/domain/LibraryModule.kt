package app.roleparty.campaign.library.domain

import app.roleparty.campaign.library.domain.port.primary.ChangeLibraryEntryMetadata
import app.roleparty.campaign.library.domain.port.primary.CreateJournalEntry
import app.roleparty.campaign.library.domain.port.primary.FindJournalEntries
import app.roleparty.campaign.library.domain.port.primary.UpdateJournalEntry
import app.roleparty.campaign.library.domain.port.primary.CreateLibraryEntry
import app.roleparty.campaign.library.domain.port.primary.FindLibraryEntries
import app.roleparty.campaign.library.domain.port.secondary.Campaigns
import app.roleparty.campaign.library.domain.port.secondary.JournalEntries
import app.roleparty.campaign.library.domain.port.secondary.PlayerCharacters
import app.roleparty.campaign.library.domain.port.secondary.LibraryEntries
import app.roleparty.campaign.library.domain.service.GameMasterCheckingService
import app.roleparty.campaign.library.domain.service.JournalEntryCreatingService
import app.roleparty.campaign.library.domain.service.JournalEntryFindingService
import app.roleparty.campaign.library.domain.service.JournalEntryUpdatingService
import app.roleparty.campaign.library.domain.service.PlayerCharacterOwnerCheckingService
import app.roleparty.campaign.library.domain.service.LibraryEntryCreatingService
import app.roleparty.campaign.library.domain.service.LibraryEntryFindingService
import app.roleparty.campaign.library.domain.service.LibraryEntryMetadataChangingService

class LibraryModule(
    libraryEntries: LibraryEntries,
    campaigns: Campaigns,
    journalEntries: JournalEntries,
    playerCharacters: PlayerCharacters,
) {

    private val gameMaster = GameMasterCheckingService(campaigns)

    private val owner = PlayerCharacterOwnerCheckingService(playerCharacters)

    val createLibraryEntry: CreateLibraryEntry = LibraryEntryCreatingService(libraryEntries, gameMaster)

    val findLibraryEntries: FindLibraryEntries = LibraryEntryFindingService(libraryEntries, gameMaster)

    val changeLibraryEntryMetadata: ChangeLibraryEntryMetadata = LibraryEntryMetadataChangingService(libraryEntries, gameMaster)

    val createJournalEntry: CreateJournalEntry = JournalEntryCreatingService(journalEntries, owner)

    val findJournalEntries: FindJournalEntries = JournalEntryFindingService(journalEntries, owner)

    val updateJournalEntry: UpdateJournalEntry = JournalEntryUpdatingService(journalEntries, owner)
}
