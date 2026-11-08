package app.roleparty.campaign.story.domain.model

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.story.commons.SceneId

sealed interface StoryError {

    data class CampaignNotFound(val campaignId: CampaignId) : StoryError

    data object GameMasterRequired : StoryError

    data object BlankName : StoryError

    data class SceneNotFound(val sceneId: SceneId) : StoryError

    data class LibraryEntryNotFound(val libraryEntryId: LibraryEntryId) : StoryError
}
