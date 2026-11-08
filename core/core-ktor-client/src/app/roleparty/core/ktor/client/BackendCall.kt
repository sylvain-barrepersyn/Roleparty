package app.roleparty.core.ktor.client

import app.roleparty.core.commons.Outcome
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.ContentConvertException
import io.ktor.serialization.kotlinx.json.json
import java.io.IOException
import java.util.Locale
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

sealed interface BackendFailure {

    data class Rejected(val code: String, val message: String) : BackendFailure

    data object Unreachable : BackendFailure

    data object Unexpected : BackendFailure
}

fun HttpClientConfig<*>.installBackend(baseUrl: String) {
    expectSuccess = false
    install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    install(Resources)
    defaultRequest {
        url(baseUrl)
        headers.append(HttpHeaders.AcceptLanguage, Locale.getDefault().language)
    }
}

suspend inline fun <reified T> backendCall(request: () -> HttpResponse): Outcome<T, BackendFailure> =
    try {
        val response = request()
        when {
            !response.status.isSuccess() -> Outcome.Failure(failureOf(response))
            T::class == Unit::class -> Outcome.Success(Unit as T)
            else -> Outcome.Success(response.body<T>())
        }
    } catch (_: IOException) {
        Outcome.Failure(BackendFailure.Unreachable)
    } catch (_: SerializationException) {
        Outcome.Failure(BackendFailure.Unexpected)
    } catch (_: ContentConvertException) {
        Outcome.Failure(BackendFailure.Unexpected)
    } catch (_: NoTransformationFoundException) {
        Outcome.Failure(BackendFailure.Unexpected)
    }

@PublishedApi
internal suspend fun failureOf(response: HttpResponse): BackendFailure =
    try {
        val error = response.body<ErrorBody>()
        BackendFailure.Rejected(error.code, error.message)
    } catch (_: SerializationException) {
        BackendFailure.Unexpected
    } catch (_: ContentConvertException) {
        BackendFailure.Unexpected
    } catch (_: NoTransformationFoundException) {
        BackendFailure.Unexpected
    } catch (_: IllegalArgumentException) {
        BackendFailure.Unexpected
    }

@Serializable
private data class ErrorBody(val code: String, val message: String)
