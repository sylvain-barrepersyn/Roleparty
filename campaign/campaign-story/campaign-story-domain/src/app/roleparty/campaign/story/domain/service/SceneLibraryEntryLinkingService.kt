package app.roleparty.campaign.story.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.story.commons.SceneId
import app.roleparty.campaign.story.domain.model.StoryError
import app.roleparty.campaign.story.domain.port.primary.LinkSceneLibraryEntry
import app.roleparty.campaign.story.domain.port.secondary.LibraryEntries
import app.roleparty.campaign.story.domain.port.secondary.Scenes
import app.roleparty.core.commons.Outcome

internal class SceneLibraryEntryLinkingService(
    private val scenes: Scenes,
    private val libraryEntries: LibraryEntries,
    private val gameMaster: GameMasterCheckingService,
) : LinkSceneLibraryEntry {

    override suspend fun link(requester: AccountId, campaignId: CampaignId, sceneId: SceneId, libraryEntryId: LibraryEntryId): Outcome<Unit, StoryError> {
        check(requester, campaignId, sceneId)?.let { return Outcome.Failure(it) }
        if (libraryEntries.campaignOf(libraryEntryId) != campaignId) return Outcome.Failure(StoryError.LibraryEntryNotFound(libraryEntryId))

        scenes.linkLibraryEntry(sceneId, libraryEntryId)
        return Outcome.success()
    }

    override suspend fun unlink(requester: AccountId, campaignId: CampaignId, sceneId: SceneId, libraryEntryId: LibraryEntryId): Outcome<Unit, StoryError> {
        check(requester, campaignId, sceneId)?.let { return Outcome.Failure(it) }

        scenes.unlinkLibraryEntry(sceneId, libraryEntryId)
        return Outcome.success()
    }

    private suspend fun check(requester: AccountId, campaignId: CampaignId, sceneId: SceneId): StoryError? {
        gameMaster.check(requester, campaignId)?.let { return it }
        return if (scenes.findById(sceneId)?.campaignId == campaignId) null else StoryError.SceneNotFound(sceneId)
    }
}
