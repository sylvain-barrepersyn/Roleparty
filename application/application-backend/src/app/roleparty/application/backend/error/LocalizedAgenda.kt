package app.roleparty.application.backend.error

import app.roleparty.application.backend.i18n.locale
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.agenda.infrastructure.adapter.primary.AgendaLocalization
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

object LocalizedAgenda : AgendaLocalization {

    override suspend fun respond(call: ApplicationCall, error: AgendaError) {
        val localized = error.localize(call.locale)
        call.respond(localized.status, localized.response)
    }
}
