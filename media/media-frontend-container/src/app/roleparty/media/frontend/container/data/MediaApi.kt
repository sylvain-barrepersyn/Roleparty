package app.roleparty.media.frontend.container.data

import app.roleparty.core.commons.Outcome
import app.roleparty.media.commons.ImageFormat
import app.roleparty.media.commons.ImageId
import app.roleparty.media.contract.web.ImageUploadedResponseBody
import app.roleparty.media.contract.web.ImagesResource
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.resources.href
import io.ktor.resources.serialization.ResourcesFormat
import java.io.IOException
import java.util.Locale
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

sealed interface MediaFailure {

    data class Rejected(val code: String, val message: String) : MediaFailure

    data object Unreachable : MediaFailure

    data object Unexpected : MediaFailure
}

interface MediaApi {

    suspend fun upload(content: ByteArray, format: ImageFormat): Outcome<ImageId, MediaFailure>

    fun imageUrl(imageId: ImageId): String
}

class HttpMediaApi(
    private val httpClient: HttpClient,
    private val baseUrl: String,
) : MediaApi {

    private val json = Json { ignoreUnknownKeys = true }

    private val resources = ResourcesFormat()

    override suspend fun upload(content: ByteArray, format: ImageFormat): Outcome<ImageId, MediaFailure> =
        try {
            val response = httpClient.post("$baseUrl${href(resources, ImagesResource())}") {
                contentType(ContentType.parse(format.mediaType))
                header(HttpHeaders.AcceptLanguage, Locale.getDefault().language)
                setBody(content)
            }
            if (response.status.isSuccess()) {
                Outcome.Success(json.decodeFromString<ImageUploadedResponseBody>(response.bodyAsText()).id)
            } else {
                Outcome.Failure(failureOf(response))
            }
        } catch (_: IOException) {
            Outcome.Failure(MediaFailure.Unreachable)
        } catch (_: SerializationException) {
            Outcome.Failure(MediaFailure.Unexpected)
        }

    override fun imageUrl(imageId: ImageId) = "$baseUrl${href(resources, ImagesResource.Image(imageId))}"

    private suspend fun failureOf(response: HttpResponse): MediaFailure =
        try {
            val error = json.decodeFromString<ErrorBody>(response.bodyAsText())
            MediaFailure.Rejected(error.code, error.message)
        } catch (_: SerializationException) {
            MediaFailure.Unexpected
        } catch (_: IllegalArgumentException) {
            MediaFailure.Unexpected
        }

    @Serializable
    private data class ErrorBody(val code: String, val message: String)
}
