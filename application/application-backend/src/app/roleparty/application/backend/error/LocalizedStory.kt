package app.roleparty.application.backend.error

import app.roleparty.application.backend.i18n.locale
import app.roleparty.campaign.story.domain.model.StoryError
import app.roleparty.campaign.story.infrastructure.adapter.primary.StoryLocalization
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

object LocalizedStory : StoryLocalization {

    override suspend fun respond(call: ApplicationCall, error: StoryError) {
        val localized = error.localize(call.locale)
        call.respond(localized.status, localized.response)
    }
}
