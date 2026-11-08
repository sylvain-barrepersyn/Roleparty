package app.roleparty.security.infrastructure.adapter.primary

import app.roleparty.communication.commons.CommunicationError
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.Outcome
import app.roleparty.core.ktor.rest.BodyLimit
import app.roleparty.core.ktor.rest.ErrorResponder
import app.roleparty.core.ktor.rest.respondOutcome
import app.roleparty.core.ktor.rest.respondStatus
import app.roleparty.core.security.authenticated
import app.roleparty.core.security.user
import app.roleparty.security.contract.web.AuthenticationResponseBody
import app.roleparty.security.contract.web.EmailPasswordSignInRequestBody
import app.roleparty.security.contract.web.EmailPasswordSignUpRequestBody
import app.roleparty.security.contract.web.EmailVerificationRequestBody
import app.roleparty.security.contract.web.PasswordChangeRequestBody
import app.roleparty.security.contract.web.PasswordResetConfirmationRequestBody
import app.roleparty.security.contract.web.PasswordResetRequestBody
import app.roleparty.security.contract.web.RefreshTokenRequestBody
import app.roleparty.security.contract.web.SecurityResource
import app.roleparty.security.domain.SecurityModule
import app.roleparty.security.domain.model.Authentication
import app.roleparty.security.domain.model.IssuedToken
import app.roleparty.security.domain.model.RefreshToken
import app.roleparty.security.domain.model.SecurityError
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import java.util.Locale

private const val MAX_BODY_BYTES = 4096L

interface SecurityLocalization {

    fun locale(call: ApplicationCall): Locale

    suspend fun respond(call: ApplicationCall, error: SecurityError)

    suspend fun respond(call: ApplicationCall, error: CommunicationError)
}

internal fun Route.securityRoutes(
    security: SecurityModule,
    localization: SecurityLocalization,
    credentialsRateLimit: RateLimitName,
    sessionsRateLimit: RateLimitName,
) {
    suspend fun ApplicationCall.withEmailAddress(value: String, action: suspend (EmailAddress) -> Unit) {
        when (val emailAddress = EmailAddress.parse(value)) {
            is Outcome.Failure -> localization.respond(this, emailAddress.error)
            is Outcome.Success -> action(emailAddress.value)
        }
    }

    val errors = ErrorResponder<SecurityError> { call, error -> localization.respond(call, error) }

    suspend fun ApplicationCall.respondAuthentication(outcome: Outcome<Authentication, SecurityError>, status: HttpStatusCode) =
        respondOutcome(outcome, errors, status) { it.toResponseBody() }

    rateLimit(credentialsRateLimit) {
        install(BodyLimit) { maxBytes = MAX_BODY_BYTES }

        post<SecurityResource.SignUp> {
            val body = call.receive<EmailPasswordSignUpRequestBody>()
            call.withEmailAddress(body.emailAddress) { emailAddress ->
                call.respondStatus(security.signUp(emailAddress, body.password, localization.locale(call)), errors, HttpStatusCode.Accepted)
            }
        }

        post<SecurityResource.VerifyEmail> {
            val body = call.receive<EmailVerificationRequestBody>()
            call.withEmailAddress(body.emailAddress) { emailAddress ->
                call.respondStatus(security.verifyEmailAddress(emailAddress, body.code), errors, HttpStatusCode.NoContent)
            }
        }

        post<SecurityResource.SignIn> {
            val body = call.receive<EmailPasswordSignInRequestBody>()
            call.withEmailAddress(body.emailAddress) { emailAddress ->
                call.respondAuthentication(security.signIn(emailAddress, body.password), HttpStatusCode.OK)
            }
        }

        post<SecurityResource.PasswordReset.Request> {
            val body = call.receive<PasswordResetRequestBody>()
            call.withEmailAddress(body.emailAddress) { emailAddress ->
                security.resetPassword.request(emailAddress, localization.locale(call))
                call.respond(HttpStatusCode.Accepted)
            }
        }

        post<SecurityResource.PasswordReset.Confirm> {
            val body = call.receive<PasswordResetConfirmationRequestBody>()
            call.withEmailAddress(body.emailAddress) { emailAddress ->
                call.respondStatus(security.resetPassword.confirm(emailAddress, body.code, body.newPassword), errors, HttpStatusCode.NoContent)
            }
        }

        authenticated {
            put<SecurityResource.Password> {
                val body = call.receive<PasswordChangeRequestBody>()
                call.respondStatus(security.changePassword(call.user().accountId, body.currentPassword, body.newPassword), errors, HttpStatusCode.NoContent)
            }
        }
    }

    rateLimit(sessionsRateLimit) {
        route("") {
            install(BodyLimit) { maxBytes = MAX_BODY_BYTES }

            post<SecurityResource.Refresh> {
                val body = call.receive<RefreshTokenRequestBody>()
                call.respondAuthentication(security.refreshAuthentication(RefreshToken.from(body.refreshToken)), HttpStatusCode.Created)
            }

            post<SecurityResource.SignOut> {
                val body = call.receive<RefreshTokenRequestBody>()
                security.signOut.fromDevice(RefreshToken.from(body.refreshToken))
                call.respond(HttpStatusCode.NoContent)
            }
        }

        authenticated {
            post<SecurityResource.SignOutEverywhere> {
                security.signOut.everywhere(call.user().accountId)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}

private fun Authentication.toResponseBody() = AuthenticationResponseBody(accessToken.toResponseBody(), refreshToken.toResponseBody())

private fun IssuedToken<*>.toResponseBody() = AuthenticationResponseBody.Token(value.toString(), expiration)
