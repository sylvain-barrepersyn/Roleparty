package app.roleparty.campaign.story.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.story.commons.SceneId
import app.roleparty.campaign.story.domain.StoryModule
import app.roleparty.core.document.TextRun
import app.roleparty.core.document.Paragraph
import app.roleparty.core.document.Document
import app.roleparty.core.test.shouldBeSuccess

class StoryFixture {

    private val scenes = InMemoryScenes()
    private val campaigns = InMemoryCampaigns()
    private val libraryEntries = InMemoryLibraryEntries()
    private val gameMasters = mutableMapOf<CampaignId, AccountId>()

    val module = StoryModule(scenes, campaigns, libraryEntries)

    val createScene = module.createScene
    val changeScene = module.changeScene
    val deleteScene = module.deleteScene
    val findScenes = module.findScenes
    val linkSceneLibraryEntry = module.linkSceneLibraryEntry

    fun `given a campaign`(gameMaster: AccountId = AccountId.generate()): CampaignId {
        val campaignId = CampaignId.generate()
        campaigns.given(campaignId, gameMaster)
        gameMasters[campaignId] = gameMaster
        return campaignId
    }

    suspend fun `given a scene`(campaignId: CampaignId, name: String = "Ambush"): SceneId =
        createScene(gameMasters.getValue(campaignId), campaignId, name, document("Goblins attack.")).shouldBeSuccess()

    fun `given a library entry`(campaignId: CampaignId): LibraryEntryId {
        val libraryEntryId = LibraryEntryId.generate()
        libraryEntries.given(libraryEntryId, campaignId)
        return libraryEntryId
    }

    suspend fun scene(sceneId: SceneId) = scenes.findById(sceneId)
}

fun document(text: String) = Document.from(listOf(Paragraph(runs = listOf(TextRun(text)))))
