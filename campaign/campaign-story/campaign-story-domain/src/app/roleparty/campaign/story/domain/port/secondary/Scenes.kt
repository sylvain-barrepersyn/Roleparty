package app.roleparty.campaign.story.domain.port.secondary

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.story.commons.SceneId
import app.roleparty.campaign.story.domain.model.Scene

interface Scenes {

    suspend fun save(scene: Scene)

    suspend fun findById(sceneId: SceneId): Scene?

    suspend fun ofCampaign(campaignId: CampaignId): List<Scene>

    suspend fun delete(sceneId: SceneId)

    suspend fun linkLibraryEntry(sceneId: SceneId, libraryEntryId: LibraryEntryId)

    suspend fun unlinkLibraryEntry(sceneId: SceneId, libraryEntryId: LibraryEntryId)
}
