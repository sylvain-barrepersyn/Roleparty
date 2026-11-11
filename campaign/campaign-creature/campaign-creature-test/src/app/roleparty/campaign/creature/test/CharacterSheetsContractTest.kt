package app.roleparty.campaign.creature.test

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.CharacterSheetId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.domain.model.CharacterSheet
import app.roleparty.campaign.creature.domain.model.CharacterStats
import app.roleparty.campaign.creature.domain.port.secondary.CharacterSheets
import app.roleparty.core.test.FixturedBehaviorTest
import app.roleparty.media.commons.ImageId
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class CharacterSheetsContractSubject(
    val characterSheets: CharacterSheets,
    val givenCampaign: suspend () -> CampaignId,
    val givenMonsterSheet: suspend (CampaignId) -> MonsterSheetId,
)

abstract class CharacterSheetsContractTest(createSubject: () -> CharacterSheetsContractSubject) : FixturedBehaviorTest() {

    private val subject = createSubject()
    private val characterSheets = subject.characterSheets

    init {
        Given("a linked character and a character with its own stats") {
            val campaignId = subject.givenCampaign()
            val monsterSheetId = subject.givenMonsterSheet(campaignId)
            val linked = CharacterSheet(CharacterSheetId.generate(), campaignId, "Strahd", ImageId.generate(), "**Vampire** lord.", CharacterStats.Inherited(monsterSheetId))
            val own = CharacterSheet(CharacterSheetId.generate(), campaignId, "Ireena", null, "", CharacterStats.Own(CreatureSamples.dragonStats()))
            characterSheets.save(linked)
            characterSheets.save(own)

            Then("they should be found with their stats, and listed by campaign in creation order") {
                characterSheets.findById(linked.id) shouldBe linked
                characterSheets.findById(own.id) shouldBe own
                characterSheets.findById(CharacterSheetId.generate()).shouldBeNull()
                characterSheets.ofCampaign(campaignId) shouldBe listOf(linked, own)
                characterSheets.ofCampaign(subject.givenCampaign()) shouldBe emptyList()
            }

            When("the linked character is saved with its own stats") {
                val unlinked = linked.copy(notes = "Unlinked.", stats = CharacterStats.Own(CreatureSamples.dragonStats()))
                characterSheets.save(unlinked)

                Then("it should be found with its own stats") {
                    characterSheets.findById(linked.id) shouldBe unlinked
                }
            }
        }
    }
}
