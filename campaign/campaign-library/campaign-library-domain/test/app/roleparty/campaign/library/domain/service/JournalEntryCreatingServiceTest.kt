package app.roleparty.campaign.library.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.library.domain.model.JournalEntry
import app.roleparty.campaign.library.domain.model.LibraryError
import app.roleparty.campaign.library.test.LibraryBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.media.commons.ImageId
import io.kotest.matchers.shouldBe

class JournalEntryCreatingServiceTest : LibraryBehaviorTest() {

    init {
        Given("a player character") {
            val player = AccountId.generate()
            val playerCharacterId = fixtures.`given a player character`(player)

            When("its player adds a journal entry") {
                val id = fixtures.createJournalEntry(player, playerCharacterId).shouldBeSuccess()

                Then("an empty entry should be kept for that character") {
                    fixtures.journalEntry(id) shouldBe JournalEntry(id, playerCharacterId, JournalEntry.Content.EMPTY)
                }
            }

            Then("another account should not add an entry to it") {
                fixtures.createJournalEntry(AccountId.generate(), playerCharacterId) shouldBeFailure LibraryError.PlayerCharacterOwnerRequired
            }
        }

        Given("an unknown player character") {
            val playerCharacterId = PlayerCharacterId.generate()

            Then("adding an entry should be refused") {
                fixtures.createJournalEntry(AccountId.generate(), playerCharacterId) shouldBeFailure LibraryError.PlayerCharacterNotFound(playerCharacterId)
            }
        }
    }
}
