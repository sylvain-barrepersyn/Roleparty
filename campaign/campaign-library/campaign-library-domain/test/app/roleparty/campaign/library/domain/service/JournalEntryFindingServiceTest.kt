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

class JournalEntryFindingServiceTest : LibraryBehaviorTest() {

    init {
        Given("two characters of the same player, each with a journal") {
            val player = AccountId.generate()
            val playerCharacterId = fixtures.`given a player character`(player)
            val older = fixtures.`given a journal entry`(player, playerCharacterId)
            val newer = fixtures.`given a journal entry`(player, playerCharacterId)
            fixtures.`given a journal entry`(player, fixtures.`given a player character`(player))

            Then("the journal of a character should list only its entries, newest first") {
                fixtures.findJournalEntries.ofPlayerCharacter(player, playerCharacterId).shouldBeSuccess().map { it.id } shouldBe listOf(newer, older)
            }

            Then("nobody else should read it, not even another account") {
                fixtures.findJournalEntries.ofPlayerCharacter(AccountId.generate(), playerCharacterId) shouldBeFailure LibraryError.PlayerCharacterOwnerRequired
            }
        }
    }
}
