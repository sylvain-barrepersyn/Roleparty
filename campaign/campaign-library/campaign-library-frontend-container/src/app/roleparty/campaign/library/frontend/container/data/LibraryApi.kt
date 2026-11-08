package app.roleparty.campaign.library.frontend.container.data

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.library.contract.web.LibraryEntryCreatedResponseBody
import app.roleparty.campaign.library.contract.web.LibraryEntryCreationRequestBody
import app.roleparty.campaign.library.contract.web.LibraryEntryMetadataRequestBody
import app.roleparty.campaign.library.contract.web.LibraryEntryResponseBody
import app.roleparty.campaign.library.contract.web.LibraryResource
import app.roleparty.core.commons.Outcome
import app.roleparty.core.commons.map
import app.roleparty.core.ktor.client.BackendFailure
import app.roleparty.core.ktor.client.backendCall
import app.roleparty.core.ktor.client.jsonBody
import app.roleparty.media.commons.ImageId
import io.ktor.client.HttpClient
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.plugins.resources.put

interface LibraryApi {

    suspend fun entries(campaignId: CampaignId): Outcome<List<LibraryEntryResponseBody>, BackendFailure>

    suspend fun createEntry(campaignId: CampaignId, title: String, description: String, imageId: ImageId): Outcome<LibraryEntryId, BackendFailure>

    suspend fun changeEntryMetadata(campaignId: CampaignId, entryId: LibraryEntryId, title: String, description: String): Outcome<Unit, BackendFailure>
}

class HttpLibraryApi(private val client: HttpClient) : LibraryApi {

    override suspend fun entries(campaignId: CampaignId) =
        backendCall<List<LibraryEntryResponseBody>> { client.get(LibraryResource(campaignId)) }

    override suspend fun createEntry(campaignId: CampaignId, title: String, description: String, imageId: ImageId) =
        backendCall<LibraryEntryCreatedResponseBody> {
            client.post(LibraryResource(campaignId)) { jsonBody(LibraryEntryCreationRequestBody(title, description, imageId)) }
        }.map { it.id }

    override suspend fun changeEntryMetadata(campaignId: CampaignId, entryId: LibraryEntryId, title: String, description: String) =
        backendCall<Unit> {
            client.put(LibraryResource.Entry.Metadata(entryOf(campaignId, entryId))) { jsonBody(LibraryEntryMetadataRequestBody(title, description)) }
        }

    private fun entryOf(campaignId: CampaignId, entryId: LibraryEntryId) = LibraryResource.Entry(LibraryResource(campaignId), entryId)
}
