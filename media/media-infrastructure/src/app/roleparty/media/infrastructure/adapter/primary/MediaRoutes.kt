package app.roleparty.media.infrastructure.adapter.primary

import app.roleparty.core.ktor.rest.ErrorResponder
import app.roleparty.core.ktor.rest.receiveBytes
import app.roleparty.core.ktor.rest.respondOutcome
import app.roleparty.core.security.authenticated
import app.roleparty.media.commons.ImageFormat
import app.roleparty.media.contract.web.ImageUploadedResponseBody
import app.roleparty.media.contract.web.ImagesResource
import app.roleparty.media.domain.MediaModule
import app.roleparty.media.domain.model.Image
import app.roleparty.media.domain.model.MediaError
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.contentType
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.Route

private const val IMMUTABLE_CACHE = "public, max-age=31536000, immutable"

interface MediaLocalization : ErrorResponder<MediaError>

internal fun Route.mediaRoutes(media: MediaModule, localization: MediaLocalization) {
    authenticated {
        post<ImagesResource> {
            val format = ImageFormat.ofMediaType(call.request.contentType().withoutParameters().toString())
                ?: return@post call.respond(HttpStatusCode.UnsupportedMediaType)

            val content = call.receiveBytes(Image.MAX_BYTES.toLong())
                ?: return@post localization.respond(call, MediaError.ImageTooLarge(Image.MAX_BYTES))

            call.respondOutcome(media.uploadImage(content, format), localization, HttpStatusCode.Created) { ImageUploadedResponseBody(it) }
        }
    }

    get<ImagesResource.Image> { resource ->
        val image = media.findImage.byId(resource.imageId) ?: return@get call.respond(HttpStatusCode.NotFound)

        call.response.header(HttpHeaders.CacheControl, IMMUTABLE_CACHE)
        call.response.header("X-Content-Type-Options", "nosniff")
        call.respondBytes(image.content, ContentType.parse(image.format.mediaType))
    }
}
