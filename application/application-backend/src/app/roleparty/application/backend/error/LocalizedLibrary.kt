package app.roleparty.application.backend.error

import app.roleparty.application.backend.i18n.locale
import app.roleparty.campaign.library.domain.model.LibraryError
import app.roleparty.campaign.library.infrastructure.adapter.primary.LibraryLocalization
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

object LocalizedLibrary : LibraryLocalization {

    override suspend fun respond(call: ApplicationCall, error: LibraryError) {
        val localized = error.localize(call.locale)
        call.respond(localized.status, localized.response)
    }
}
