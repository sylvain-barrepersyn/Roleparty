package app.roleparty.campaign.library.test

import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.library.commons.JournalEntryId
import app.roleparty.campaign.library.domain.model.JournalEntry
import app.roleparty.campaign.library.domain.port.secondary.JournalEntries
import app.roleparty.core.test.FixturedBehaviorTest
import app.roleparty.media.commons.ImageId
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class JournalEntriesContractSubject(val journalEntries: JournalEntries, val givenPlayerCharacter: suspend () -> PlayerCharacterId)

abstract class JournalEntriesContractTest(createSubject: () -> JournalEntriesContractSubject) : FixturedBehaviorTest() {

    private val subject = createSubject()
    private val journalEntries = subject.journalEntries

    init {
        Given("journal entries of two player characters") {
            val playerCharacterId = subject.givenPlayerCharacter()
            val first = JournalEntry(JournalEntryId.generate(), playerCharacterId, JournalEntry.Content("Day 1", "# Arrival\n\n- Met **Ireena**", listOf(ImageId.generate(), ImageId.generate())))
            val second = JournalEntry(JournalEntryId.generate(), playerCharacterId, JournalEntry.Content.EMPTY)
            val other = JournalEntry(JournalEntryId.generate(), subject.givenPlayerCharacter(), JournalEntry.Content("Elsewhere", "", emptyList()))
            listOf(first, second, other).forEach { journalEntries.save(it) }

            Then("they should be found by id with their images in order, and listed by player character in creation order") {
                journalEntries.findById(first.id) shouldBe first
                journalEntries.findById(JournalEntryId.generate()).shouldBeNull()
                journalEntries.ofPlayerCharacter(playerCharacterId) shouldBe listOf(first, second)
            }

            When("an entry is saved with other content and images") {
                val changed = first.copy(content = JournalEntry.Content("Day one", "Rewritten.", listOf(first.content.images.last(), ImageId.generate())))
                journalEntries.save(changed)

                Then("it should be found as changed") {
                    journalEntries.findById(first.id) shouldBe changed
                }
            }
        }
    }
}
