package app.roleparty.security.frontend.container.data

import app.roleparty.core.commons.Outcome
import app.roleparty.security.contract.web.AuthenticationResponseBody
import app.roleparty.security.contract.web.EmailPasswordSignInRequestBody
import app.roleparty.security.contract.web.EmailPasswordSignUpRequestBody
import app.roleparty.security.contract.web.EmailVerificationRequestBody
import app.roleparty.security.contract.web.PasswordResetConfirmationRequestBody
import app.roleparty.security.contract.web.PasswordResetRequestBody
import app.roleparty.security.contract.web.RefreshTokenRequestBody
import app.roleparty.security.contract.web.SecurityResource
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

sealed interface SecurityFailure {

    data class Rejected(val code: String, val message: String) : SecurityFailure

    data object Unreachable : SecurityFailure

    data object Unexpected : SecurityFailure
}

interface SecurityApi {

    suspend fun signUp(emailAddress: String, password: String): Outcome<Unit, SecurityFailure>

    suspend fun verifyEmailAddress(emailAddress: String, code: String): Outcome<Unit, SecurityFailure>

    suspend fun signIn(emailAddress: String, password: String): Outcome<StoredSession, SecurityFailure>

    suspend fun refresh(refreshToken: String): Outcome<StoredSession, SecurityFailure>

    suspend fun signOut(refreshToken: String): Outcome<Unit, SecurityFailure>

    suspend fun requestPasswordReset(emailAddress: String): Outcome<Unit, SecurityFailure>

    suspend fun resetPassword(emailAddress: String, code: String, newPassword: String): Outcome<Unit, SecurityFailure>
}

class HttpSecurityApi(
    private val httpClient: HttpClient,
    private val baseUrl: String,
) : SecurityApi {

    private val json = Json { ignoreUnknownKeys = true }

    private val resources = ResourcesFormat()

    override suspend fun signUp(emailAddress: String, password: String) =
        postForNothing(path(SecurityResource.SignUp()), json.encodeToString(EmailPasswordSignUpRequestBody(emailAddress, password)))

    override suspend fun verifyEmailAddress(emailAddress: String, code: String) =
        postForNothing(path(SecurityResource.VerifyEmail()), json.encodeToString(EmailVerificationRequestBody(emailAddress, code)))

    override suspend fun signIn(emailAddress: String, password: String) =
        postForSession(path(SecurityResource.SignIn()), json.encodeToString(EmailPasswordSignInRequestBody(emailAddress, password)))

    override suspend fun refresh(refreshToken: String) =
        postForSession(path(SecurityResource.Refresh()), json.encodeToString(RefreshTokenRequestBody(refreshToken)))

    override suspend fun signOut(refreshToken: String) =
        postForNothing(path(SecurityResource.SignOut()), json.encodeToString(RefreshTokenRequestBody(refreshToken)))

    override suspend fun requestPasswordReset(emailAddress: String) =
        postForNothing(path(SecurityResource.PasswordReset.Request()), json.encodeToString(PasswordResetRequestBody(emailAddress)))

    override suspend fun resetPassword(emailAddress: String, code: String, newPassword: String) =
        postForNothing(path(SecurityResource.PasswordReset.Confirm()), json.encodeToString(PasswordResetConfirmationRequestBody(emailAddress, code, newPassword)))

    private inline fun <reified T : Any> path(resource: T) = href(resources, resource)

    private suspend fun postForNothing(path: String, body: String): Outcome<Unit, SecurityFailure> = post(path, body) { }

    private suspend fun postForSession(path: String, body: String): Outcome<StoredSession, SecurityFailure> =
        post(path, body) { response ->
            val tokens = json.decodeFromString<AuthenticationResponseBody>(response.bodyAsText())
            StoredSession(tokens.accessToken.value, tokens.refreshToken.value)
        }

    private suspend fun <T> post(path: String, body: String, onSuccess: suspend (HttpResponse) -> T): Outcome<T, SecurityFailure> =
        try {
            val response = httpClient.post("$baseUrl$path") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.AcceptLanguage, Locale.getDefault().language)
                setBody(body)
            }
            if (response.status.isSuccess()) Outcome.Success(onSuccess(response)) else Outcome.Failure(failureOf(response))
        } catch (_: IOException) {
            Outcome.Failure(SecurityFailure.Unreachable)
        } catch (_: SerializationException) {
            Outcome.Failure(SecurityFailure.Unexpected)
        }

    private suspend fun failureOf(response: HttpResponse): SecurityFailure =
        try {
            val error = json.decodeFromString<ErrorBody>(response.bodyAsText())
            SecurityFailure.Rejected(error.code, error.message)
        } catch (_: SerializationException) {
            SecurityFailure.Unexpected
        } catch (_: IllegalArgumentException) {
            SecurityFailure.Unexpected
        }

    @Serializable
    private data class ErrorBody(val code: String, val message: String)
}
