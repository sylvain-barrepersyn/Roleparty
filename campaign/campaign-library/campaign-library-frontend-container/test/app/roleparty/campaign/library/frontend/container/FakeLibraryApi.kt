package app.roleparty.campaign.library.frontend.container

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.library.contract.web.LibraryEntryResponseBody
import app.roleparty.campaign.library.frontend.container.data.LibraryApi
import app.roleparty.core.commons.Outcome
import app.roleparty.core.ktor.client.BackendFailure
import app.roleparty.media.commons.ImageId

internal class FakeLibraryApi : LibraryApi {

    val campaignId: CampaignId = CampaignId.generate()
    val calls = mutableListOf<String>()
    var failure: BackendFailure? = null
    val entries = mutableListOf(entry("Village"), entry("Castle"))
    val createdEntryId: LibraryEntryId = LibraryEntryId.generate()

    private fun <T> answer(call: String, value: () -> T): Outcome<T, BackendFailure> {
        calls += call
        return failure?.let { Outcome.Failure(it) } ?: Outcome.Success(value())
    }

    override suspend fun entries(campaignId: CampaignId) = answer("entries") { entries.toList() }

    override suspend fun createEntry(campaignId: CampaignId, title: String, description: String, imageId: ImageId) =
        answer("create $title|$description") { createdEntryId.also { entries += LibraryEntryResponseBody(it, title, description, imageId) } }

    override suspend fun changeEntryMetadata(campaignId: CampaignId, entryId: LibraryEntryId, title: String, description: String) =
        answer("change $title|$description") { }

    private fun entry(title: String) = LibraryEntryResponseBody(LibraryEntryId.generate(), title, "", ImageId.generate())
}
