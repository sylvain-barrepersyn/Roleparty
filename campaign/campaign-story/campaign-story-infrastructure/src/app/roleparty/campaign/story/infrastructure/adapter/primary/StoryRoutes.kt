package app.roleparty.campaign.story.infrastructure.adapter.primary

import app.roleparty.campaign.story.contract.web.SceneCreatedResponseBody
import app.roleparty.campaign.story.contract.web.SceneRequestBody
import app.roleparty.campaign.story.contract.web.SceneResponseBody
import app.roleparty.campaign.story.contract.web.ScenesResource
import app.roleparty.campaign.story.domain.StoryModule
import app.roleparty.campaign.story.domain.model.StoryError
import app.roleparty.core.ktor.rest.ErrorResponder
import app.roleparty.core.ktor.rest.respondOutcome
import app.roleparty.core.ktor.rest.respondStatus
import app.roleparty.core.security.authenticated
import app.roleparty.core.security.user
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.routing.Route

interface StoryLocalization : ErrorResponder<StoryError>

internal fun Route.storyRoutes(story: StoryModule, localization: StoryLocalization) {
    authenticated {
        post<ScenesResource> { resource ->
            val body = call.receive<SceneRequestBody>()
            call.respondOutcome(story.createScene(call.user().accountId, resource.campaignId, body.name, body.description), localization, HttpStatusCode.Created) {
                SceneCreatedResponseBody(it)
            }
        }

        get<ScenesResource> { resource ->
            call.respondOutcome(story.findScenes.ofCampaign(call.user().accountId, resource.campaignId), localization) { scenes ->
                scenes.map { SceneResponseBody(it.id, it.name, it.description, it.libraryEntries) }
            }
        }

        put<ScenesResource.Scene> { resource ->
            val body = call.receive<SceneRequestBody>()
            call.respondStatus(story.changeScene(call.user().accountId, resource.parent.campaignId, resource.sceneId, body.name, body.description), localization)
        }

        delete<ScenesResource.Scene> { resource ->
            call.respondStatus(story.deleteScene(call.user().accountId, resource.parent.campaignId, resource.sceneId), localization)
        }

        put<ScenesResource.Scene.LibraryEntry> { resource ->
            val scene = resource.parent
            call.respondStatus(story.linkSceneLibraryEntry.link(call.user().accountId, scene.parent.campaignId, scene.sceneId, resource.entryId), localization)
        }

        delete<ScenesResource.Scene.LibraryEntry> { resource ->
            val scene = resource.parent
            call.respondStatus(story.linkSceneLibraryEntry.unlink(call.user().accountId, scene.parent.campaignId, scene.sceneId, resource.entryId), localization)
        }
    }
}
