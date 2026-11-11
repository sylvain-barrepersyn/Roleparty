package app.roleparty.campaign.library.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.library.commons.JournalEntryId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.library.domain.LibraryModule
import app.roleparty.campaign.library.domain.model.LibraryEntry
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.media.commons.ImageId
import io.kotest.matchers.nulls.shouldNotBeNull

class LibraryFixture {

    private val libraryEntries = InMemoryLibraryEntries()
    private val campaigns = InMemoryCampaigns()
    private val journalEntries = InMemoryJournalEntries()
    private val playerCharacters = InMemoryPlayerCharacters()

    val module = LibraryModule(libraryEntries, campaigns, journalEntries, playerCharacters)

    val createLibraryEntry = module.createLibraryEntry
    val findLibraryEntries = module.findLibraryEntries
    val changeLibraryEntryMetadata = module.changeLibraryEntryMetadata
    val createJournalEntry = module.createJournalEntry
    val findJournalEntries = module.findJournalEntries
    val updateJournalEntry = module.updateJournalEntry

    fun `given a campaign`(gameMaster: AccountId = AccountId.generate()): CampaignId {
        val campaignId = CampaignId.generate()
        campaigns.given(campaignId, gameMaster)
        return campaignId
    }

    suspend fun `given a library entry`(gameMaster: AccountId, campaignId: CampaignId, title: String = "Cave"): LibraryEntryId =
        createLibraryEntry(gameMaster, campaignId, LibraryEntry.Metadata(title, "Dark."), ImageId.generate()).shouldBeSuccess()

    suspend fun libraryEntry(libraryEntryId: LibraryEntryId) = libraryEntries.findById(libraryEntryId).shouldNotBeNull()

    fun `given a player character`(player: AccountId = AccountId.generate()): PlayerCharacterId {
        val playerCharacterId = PlayerCharacterId.generate()
        playerCharacters.given(playerCharacterId, player)
        return playerCharacterId
    }

    suspend fun `given a journal entry`(player: AccountId, playerCharacterId: PlayerCharacterId): JournalEntryId =
        createJournalEntry(player, playerCharacterId).shouldBeSuccess()

    suspend fun journalEntry(journalEntryId: JournalEntryId) = journalEntries.findById(journalEntryId).shouldNotBeNull()
}
