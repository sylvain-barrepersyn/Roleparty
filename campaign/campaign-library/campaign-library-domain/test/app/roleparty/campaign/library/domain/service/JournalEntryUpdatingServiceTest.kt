package app.roleparty.campaign.library.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.library.commons.JournalEntryId
import app.roleparty.campaign.library.domain.model.JournalEntry
import app.roleparty.campaign.library.domain.model.LibraryError
import app.roleparty.campaign.library.test.LibraryBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.media.commons.ImageId
import io.kotest.matchers.shouldBe

class JournalEntryUpdatingServiceTest : LibraryBehaviorTest() {

    init {
        Given("a journal entry") {
            val player = AccountId.generate()
            val playerCharacterId = fixtures.`given a player character`(player)
            val id = fixtures.`given a journal entry`(player, playerCharacterId)
            val image = ImageId.generate()

            When("its player writes it and attaches the same image twice") {
                val content = JournalEntry.Content(" Day 1 ", "# Barovia\n\n- Fog everywhere", listOf(image, image))
                fixtures.updateJournalEntry(player, playerCharacterId, id, content).shouldBeSuccess()

                Then("the title should be trimmed, the text kept as written, and the image attached once") {
                    fixtures.journalEntry(id).content shouldBe JournalEntry.Content("Day 1", "# Barovia\n\n- Fog everywhere", listOf(image))
                }
            }

            Then("another account should not change it") {
                fixtures.updateJournalEntry(AccountId.generate(), playerCharacterId, id, JournalEntry.Content.EMPTY) shouldBeFailure
                    LibraryError.PlayerCharacterOwnerRequired
            }

            Then("it should not be changed through another character of the same player") {
                val other = fixtures.`given a player character`(player)
                fixtures.updateJournalEntry(player, other, id, JournalEntry.Content.EMPTY) shouldBeFailure LibraryError.JournalEntryNotFound(id)
            }

            Then("an unknown entry should not be found") {
                val unknown = JournalEntryId.generate()
                fixtures.updateJournalEntry(player, playerCharacterId, unknown, JournalEntry.Content.EMPTY) shouldBeFailure LibraryError.JournalEntryNotFound(unknown)
            }
        }
    }
}
