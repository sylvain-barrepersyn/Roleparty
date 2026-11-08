package app.roleparty.campaign.library.infrastructure.adapter.primary

import app.roleparty.campaign.library.contract.web.LibraryEntryCreatedResponseBody
import app.roleparty.campaign.library.contract.web.LibraryEntryCreationRequestBody
import app.roleparty.campaign.library.contract.web.LibraryEntryMetadataRequestBody
import app.roleparty.campaign.library.contract.web.LibraryEntryResponseBody
import app.roleparty.campaign.library.contract.web.LibraryResource
import app.roleparty.campaign.library.domain.LibraryModule
import app.roleparty.campaign.library.domain.model.LibraryEntry
import app.roleparty.campaign.library.domain.model.LibraryError
import app.roleparty.core.ktor.rest.ErrorResponder
import app.roleparty.core.ktor.rest.respondOutcome
import app.roleparty.core.ktor.rest.respondStatus
import app.roleparty.core.security.authenticated
import app.roleparty.core.security.user
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.routing.Route

interface LibraryLocalization : ErrorResponder<LibraryError>

internal fun Route.libraryRoutes(library: LibraryModule, localization: LibraryLocalization) {
    authenticated {
        post<LibraryResource> { resource ->
            val body = call.receive<LibraryEntryCreationRequestBody>()
            val outcome = library.createLibraryEntry(call.user().accountId, resource.campaignId, LibraryEntry.Metadata(body.title, body.description), body.imageId)
            call.respondOutcome(outcome, localization, HttpStatusCode.Created) { LibraryEntryCreatedResponseBody(it) }
        }

        get<LibraryResource> { resource ->
            call.respondOutcome(library.findLibraryEntries.ofCampaign(call.user().accountId, resource.campaignId), localization) { entries ->
                entries.map { it.toResponseBody() }
            }
        }

        put<LibraryResource.Entry.Metadata> { resource ->
            val entry = resource.parent
            val body = call.receive<LibraryEntryMetadataRequestBody>()
            val metadata = LibraryEntry.Metadata(body.title, body.description)
            call.respondStatus(library.changeLibraryEntryMetadata(call.user().accountId, entry.parent.campaignId, entry.entryId, metadata), localization)
        }

    }
}

private fun LibraryEntry.toResponseBody() = LibraryEntryResponseBody(id, metadata.title, metadata.description, image)
