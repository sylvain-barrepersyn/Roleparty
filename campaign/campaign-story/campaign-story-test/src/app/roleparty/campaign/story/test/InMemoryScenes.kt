package app.roleparty.campaign.story.test

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.story.commons.SceneId
import app.roleparty.campaign.story.domain.model.Scene
import app.roleparty.campaign.story.domain.port.secondary.Scenes

internal class InMemoryScenes : Scenes {

    private val scenes = linkedMapOf<SceneId, Scene>()

    override suspend fun save(scene: Scene) {
        scenes[scene.id] = scene.copy(libraryEntries = scenes[scene.id]?.libraryEntries.orEmpty())
    }

    override suspend fun findById(sceneId: SceneId) = scenes[sceneId]

    override suspend fun ofCampaign(campaignId: CampaignId) = scenes.values.filter { it.campaignId == campaignId }

    override suspend fun delete(sceneId: SceneId) {
        scenes.remove(sceneId)
    }

    override suspend fun linkLibraryEntry(sceneId: SceneId, libraryEntryId: LibraryEntryId) {
        val scene = scenes[sceneId] ?: return
        if (libraryEntryId !in scene.libraryEntries) scenes[sceneId] = scene.copy(libraryEntries = scene.libraryEntries + libraryEntryId)
    }

    override suspend fun unlinkLibraryEntry(sceneId: SceneId, libraryEntryId: LibraryEntryId) {
        val scene = scenes[sceneId] ?: return
        scenes[sceneId] = scene.copy(libraryEntries = scene.libraryEntries - libraryEntryId)
    }
}
