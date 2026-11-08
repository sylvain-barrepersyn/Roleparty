package app.roleparty.campaign.story.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.story.domain.model.StoryError
import app.roleparty.campaign.story.test.StoryBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe

class SceneLibraryEntryLinkingServiceTest : StoryBehaviorTest() {

    private val linkSceneLibraryEntry = fixtures.linkSceneLibraryEntry

    init {
        Given("a scene and a library entry of a campaign") {
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)
            val sceneId = fixtures.`given a scene`(campaignId)
            val entryId = fixtures.`given a library entry`(campaignId)

            When("the game master links then unlinks the entry") {
                linkSceneLibraryEntry.link(gameMaster, campaignId, sceneId, entryId).shouldBeSuccess()
                val linked = fixtures.scene(sceneId)?.libraryEntries
                linkSceneLibraryEntry.unlink(gameMaster, campaignId, sceneId, entryId).shouldBeSuccess()

                Then("the scene should have held the entry, then not anymore") {
                    linked shouldBe listOf(entryId)
                    fixtures.scene(sceneId)?.libraryEntries shouldBe emptyList()
                }
            }

            When("linking an entry or a scene of another campaign") {
                val otherCampaign = fixtures.`given a campaign`(gameMaster)
                val otherEntry = fixtures.`given a library entry`(otherCampaign)
                val otherScene = fixtures.`given a scene`(otherCampaign)

                Then("it should be refused") {
                    linkSceneLibraryEntry.link(gameMaster, campaignId, sceneId, otherEntry) shouldBeFailure StoryError.LibraryEntryNotFound(otherEntry)
                    linkSceneLibraryEntry.link(gameMaster, campaignId, otherScene, entryId) shouldBeFailure StoryError.SceneNotFound(otherScene)
                }
            }

            Then("someone else than the game master should be refused") {
                linkSceneLibraryEntry.link(AccountId.generate(), campaignId, sceneId, entryId) shouldBeFailure StoryError.GameMasterRequired
            }
        }
    }
}
