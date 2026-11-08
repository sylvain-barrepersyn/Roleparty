package app.roleparty.campaign.story.test

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.story.commons.SceneId
import app.roleparty.campaign.story.domain.model.Scene
import app.roleparty.campaign.story.domain.port.secondary.Scenes
import app.roleparty.core.test.FixturedBehaviorTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class ScenesContractSubject(
    val scenes: Scenes,
    val givenCampaign: suspend () -> CampaignId,
    val givenLibraryEntry: suspend (CampaignId) -> LibraryEntryId,
)

abstract class ScenesContractTest(createSubject: () -> ScenesContractSubject) : FixturedBehaviorTest() {

    private val subject = createSubject()
    private val scenes = subject.scenes

    init {
        Given("a scene of a campaign") {
            val campaignId = subject.givenCampaign()
            val scene = Scene(SceneId.generate(), campaignId, "Ambush", "Goblins attack.")
            scenes.save(scene)

            Then("it should be found and listed by campaign") {
                scenes.findById(scene.id) shouldBe scene
                scenes.findById(SceneId.generate()).shouldBeNull()
                scenes.ofCampaign(campaignId) shouldBe listOf(scene)
                scenes.ofCampaign(subject.givenCampaign()) shouldBe emptyList()
            }

            When("the scene is saved again") {
                scenes.save(scene.copy(name = "Big ambush"))

                Then("it should hold the new name") {
                    scenes.findById(scene.id) shouldBe scene.copy(name = "Big ambush")
                }
            }

            When("library entries are linked, one twice, then one unlinked") {
                val cave = subject.givenLibraryEntry(campaignId)
                val forest = subject.givenLibraryEntry(campaignId)
                scenes.linkLibraryEntry(scene.id, cave)
                scenes.linkLibraryEntry(scene.id, forest)
                scenes.linkLibraryEntry(scene.id, cave)
                scenes.unlinkLibraryEntry(scene.id, forest)

                Then("the scene should hold the remaining entry once, also when listed") {
                    scenes.findById(scene.id)?.libraryEntries shouldBe listOf(cave)
                    scenes.ofCampaign(campaignId).single().libraryEntries shouldBe listOf(cave)
                }

                Then("saving the scene again should keep its entries") {
                    scenes.save(scene.copy(name = "Big ambush"))
                    scenes.findById(scene.id) shouldBe scene.copy(name = "Big ambush", libraryEntries = listOf(cave))
                }
            }

            When("the scene is deleted") {
                scenes.linkLibraryEntry(scene.id, subject.givenLibraryEntry(campaignId))
                scenes.delete(scene.id)

                Then("it should not be found anymore") {
                    scenes.findById(scene.id).shouldBeNull()
                    scenes.ofCampaign(campaignId) shouldBe emptyList()
                }
            }
        }
    }
}
