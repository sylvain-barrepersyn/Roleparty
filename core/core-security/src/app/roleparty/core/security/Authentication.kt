package app.roleparty.core.security

import app.roleparty.account.commons.AccountId
import app.roleparty.core.commons.requireValueOrFile
import app.roleparty.core.commons.valueOrFile
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.principal
import io.ktor.server.routing.Route

private const val AUTHENTICATION_BEARER = "auth-bearer"
private const val PUBLIC_KEY = "JWT_PUBLIC_KEY"
private const val PREVIOUS_PUBLIC_KEY = "JWT_PREVIOUS_PUBLIC_KEY"

data class ApplicationUser(val accountId: AccountId)

fun Route.authenticated(build: Route.() -> Unit) = authenticate(AUTHENTICATION_BEARER, build = build)

fun ApplicationCall.user(): ApplicationUser = checkNotNull(principal<ApplicationUser>()) { "This route is not authenticated" }

fun Application.installAuthentication(env: Map<String, String> = System.getenv()) {
    val publicKeys = listOfNotNull(env.requireValueOrFile(PUBLIC_KEY), env.valueOrFile(PREVIOUS_PUBLIC_KEY)).map(AccessTokenKeys::publicKey)
    val verifiers: Map<String, JWTVerifier> = publicKeys.associate { key ->
        AccessTokenKeys.idOf(key) to JWT.require(Algorithm.ECDSA256(key, null)).withIssuer(AccessTokenKeys.ISSUER).withClaimPresence("sub").build()
    }

    install(Authentication) {
        jwt(AUTHENTICATION_BEARER) {
            verifier { header -> keyIdOf(header)?.let(verifiers::get) }
            validate { credential -> credential.subject?.let { ApplicationUser(AccountId.from(it)) } }
        }
    }
}

private fun keyIdOf(header: HttpAuthHeader): String? {
    val token = (header as? HttpAuthHeader.Single)?.blob ?: return null
    return try {
        JWT.decode(token).keyId
    } catch (_: JWTDecodeException) {
        null
    }
}
