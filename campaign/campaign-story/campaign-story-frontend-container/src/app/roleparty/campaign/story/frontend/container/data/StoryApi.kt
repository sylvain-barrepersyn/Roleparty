package app.roleparty.campaign.story.frontend.container.data

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.library.contract.web.LibraryEntryResponseBody
import app.roleparty.campaign.library.contract.web.LibraryResource
import app.roleparty.campaign.story.commons.SceneId
import app.roleparty.campaign.story.contract.web.SceneCreatedResponseBody
import app.roleparty.campaign.story.contract.web.SceneRequestBody
import app.roleparty.campaign.story.contract.web.SceneResponseBody
import app.roleparty.campaign.story.contract.web.ScenesResource
import app.roleparty.core.commons.Outcome
import app.roleparty.core.commons.map
import app.roleparty.core.ktor.client.BackendFailure
import app.roleparty.core.ktor.client.backendCall
import app.roleparty.core.ktor.client.jsonBody
import io.ktor.client.HttpClient
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.plugins.resources.put

interface StoryApi {

    suspend fun scenes(campaignId: CampaignId): Outcome<List<SceneResponseBody>, BackendFailure>

    suspend fun createScene(campaignId: CampaignId, name: String, description: String): Outcome<SceneId, BackendFailure>

    suspend fun changeScene(campaignId: CampaignId, sceneId: SceneId, name: String, description: String): Outcome<Unit, BackendFailure>

    suspend fun deleteScene(campaignId: CampaignId, sceneId: SceneId): Outcome<Unit, BackendFailure>

    suspend fun libraryEntries(campaignId: CampaignId): Outcome<List<LibraryEntryResponseBody>, BackendFailure>

    suspend fun linkLibraryEntry(campaignId: CampaignId, sceneId: SceneId, libraryEntryId: LibraryEntryId): Outcome<Unit, BackendFailure>

    suspend fun unlinkLibraryEntry(campaignId: CampaignId, sceneId: SceneId, libraryEntryId: LibraryEntryId): Outcome<Unit, BackendFailure>
}

class HttpStoryApi(private val client: HttpClient) : StoryApi {

    override suspend fun scenes(campaignId: CampaignId) =
        backendCall<List<SceneResponseBody>> { client.get(ScenesResource(campaignId)) }

    override suspend fun createScene(campaignId: CampaignId, name: String, description: String) =
        backendCall<SceneCreatedResponseBody> {
            client.post(ScenesResource(campaignId)) { jsonBody(SceneRequestBody(name, description)) }
        }.map { it.id }

    override suspend fun changeScene(campaignId: CampaignId, sceneId: SceneId, name: String, description: String) =
        backendCall<Unit> { client.put(sceneOf(campaignId, sceneId)) { jsonBody(SceneRequestBody(name, description)) } }

    override suspend fun deleteScene(campaignId: CampaignId, sceneId: SceneId) =
        backendCall<Unit> { client.delete(sceneOf(campaignId, sceneId)) }

    override suspend fun libraryEntries(campaignId: CampaignId) =
        backendCall<List<LibraryEntryResponseBody>> { client.get(LibraryResource(campaignId)) }

    override suspend fun linkLibraryEntry(campaignId: CampaignId, sceneId: SceneId, libraryEntryId: LibraryEntryId) =
        backendCall<Unit> { client.put(ScenesResource.Scene.LibraryEntry(sceneOf(campaignId, sceneId), libraryEntryId)) }

    override suspend fun unlinkLibraryEntry(campaignId: CampaignId, sceneId: SceneId, libraryEntryId: LibraryEntryId) =
        backendCall<Unit> { client.delete(ScenesResource.Scene.LibraryEntry(sceneOf(campaignId, sceneId), libraryEntryId)) }

    private fun sceneOf(campaignId: CampaignId, sceneId: SceneId) = ScenesResource.Scene(ScenesResource(campaignId), sceneId)
}
