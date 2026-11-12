package app.roleparty.application.backend.error

import app.roleparty.application.backend.i18n.locale
import app.roleparty.campaign.tabletop.domain.model.TabletopError
import app.roleparty.campaign.tabletop.infrastructure.adapter.primary.TabletopLocalization
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

object LocalizedTabletop : TabletopLocalization {

    override suspend fun respond(call: ApplicationCall, error: TabletopError) {
        val localized = error.localize(call.locale)
        call.respond(localized.status, localized.response)
    }
}
