package app.roleparty.application.backend.error

import app.roleparty.application.backend.i18n.locale
import app.roleparty.media.domain.model.MediaError
import app.roleparty.media.infrastructure.adapter.primary.MediaLocalization
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

object LocalizedMedia : MediaLocalization {

    override suspend fun respond(call: ApplicationCall, error: MediaError) {
        val localized = error.localize(call.locale)
        call.respond(localized.status, localized.response)
    }
}
