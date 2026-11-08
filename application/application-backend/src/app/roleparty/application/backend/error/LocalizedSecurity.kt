package app.roleparty.application.backend.error

import app.roleparty.application.backend.i18n.locale
import app.roleparty.communication.commons.CommunicationError
import app.roleparty.security.domain.model.SecurityError
import app.roleparty.security.infrastructure.adapter.primary.SecurityLocalization
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

object LocalizedSecurity : SecurityLocalization {

    override fun locale(call: ApplicationCall) = call.locale

    override suspend fun respond(call: ApplicationCall, error: SecurityError) =
        call.respond(error.localize(call.locale))

    override suspend fun respond(call: ApplicationCall, error: CommunicationError) =
        call.respond(error.localize(call.locale))

    private suspend fun ApplicationCall.respond(error: LocalizedError) = respond(error.status, error.response)
}
