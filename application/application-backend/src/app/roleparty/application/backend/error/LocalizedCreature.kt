package app.roleparty.application.backend.error

import app.roleparty.application.backend.i18n.locale
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.infrastructure.adapter.primary.CreatureLocalization
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

object LocalizedCreature : CreatureLocalization {

    override suspend fun respond(call: ApplicationCall, error: CreatureError) {
        val localized = error.localize(call.locale)
        call.respond(localized.status, localized.response)
    }
}
