package app.roleparty.campaign.story.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.story.commons.SceneId
import app.roleparty.campaign.story.domain.model.StoryError
import app.roleparty.core.commons.Outcome

interface LinkSceneLibraryEntry {

    suspend fun link(requester: AccountId, campaignId: CampaignId, sceneId: SceneId, libraryEntryId: LibraryEntryId): Outcome<Unit, StoryError>

    suspend fun unlink(requester: AccountId, campaignId: CampaignId, sceneId: SceneId, libraryEntryId: LibraryEntryId): Outcome<Unit, StoryError>
}
