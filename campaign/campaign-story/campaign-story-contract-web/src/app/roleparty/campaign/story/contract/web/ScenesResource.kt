package app.roleparty.campaign.story.contract.web

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.story.commons.SceneId
import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/v1/campaigns/{campaignId}/scenes")
class ScenesResource(val campaignId: CampaignId) {

    @Serializable
    @Resource("{sceneId}")
    class Scene(val parent: ScenesResource, val sceneId: SceneId) {

        @Serializable
        @Resource("library/{entryId}")
        class LibraryEntry(val parent: Scene, val entryId: LibraryEntryId)
    }
}
