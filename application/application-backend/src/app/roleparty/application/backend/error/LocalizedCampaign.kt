package app.roleparty.application.backend.error

import app.roleparty.application.backend.i18n.locale
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.campaign.infrastructure.adapter.primary.CampaignLocalization
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

object LocalizedCampaign : CampaignLocalization {

    override suspend fun respond(call: ApplicationCall, error: CampaignError) {
        val localized = error.localize(call.locale)
        call.respond(localized.status, localized.response)
    }
}
