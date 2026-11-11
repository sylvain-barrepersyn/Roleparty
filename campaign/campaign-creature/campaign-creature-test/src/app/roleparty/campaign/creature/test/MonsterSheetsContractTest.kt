package app.roleparty.campaign.creature.test

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.domain.model.MonsterSheet
import app.roleparty.campaign.creature.domain.model.MonsterSheetOverview
import app.roleparty.campaign.creature.domain.port.secondary.MonsterSheets
import app.roleparty.core.test.FixturedBehaviorTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class MonsterSheetsContractSubject(val monsterSheets: MonsterSheets, val givenCampaign: suspend () -> CampaignId)

abstract class MonsterSheetsContractTest(createSubject: () -> MonsterSheetsContractSubject) : FixturedBehaviorTest() {

    private val subject = createSubject()
    private val monsterSheets = subject.monsterSheets

    init {
        Given("two monster sheets of a campaign") {
            val campaignId = subject.givenCampaign()
            val empty = MonsterSheet.empty(MonsterSheetId.generate(), campaignId)
            val dragon = CreatureSamples.dragon(campaignId)
            monsterSheets.save(empty)
            monsterSheets.save(dragon)

            Then("they should be found with all their content, and listed as overviews") {
                monsterSheets.findById(dragon.id) shouldBe dragon
                monsterSheets.findById(empty.id) shouldBe empty
                monsterSheets.findById(MonsterSheetId.generate()).shouldBeNull()
                monsterSheets.overviewsOf(campaignId) shouldBe listOf(empty, dragon).map {
                    MonsterSheetOverview(it.id, it.name, it.image, it.stats.type, it.stats.size, it.stats.challengeRating)
                }
                monsterSheets.overviewsOf(subject.givenCampaign()) shouldBe emptyList()
            }

            When("one is saved again with changes") {
                val changed = dragon.copy(name = "Adult Red Dragon", image = null, stats = dragon.stats.copy(actions = emptyList()))
                monsterSheets.save(changed)

                Then("it should be found as changed") {
                    monsterSheets.findById(dragon.id) shouldBe changed
                }
            }
        }
    }
}
