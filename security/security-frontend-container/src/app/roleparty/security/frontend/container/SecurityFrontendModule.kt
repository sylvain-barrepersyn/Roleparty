package app.roleparty.security.frontend.container

import app.roleparty.core.ktor.client.installBackend
import app.roleparty.security.frontend.container.authentication.AuthenticationViewModel
import app.roleparty.security.frontend.container.data.AuthenticatedBackend
import app.roleparty.security.frontend.container.data.HttpSecurityApi
import app.roleparty.security.frontend.container.data.KSafeSessionStore
import app.roleparty.security.frontend.container.data.SecurityApi
import app.roleparty.security.frontend.container.data.SessionStore
import app.roleparty.security.frontend.container.data.installApplicationAuthentication
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.dsl.onClose

private const val BACKEND_URL = "ROLEPARTY_BACKEND_URL"
private const val PACKAGED_BACKEND_URL = "roleparty.backend.url"
private const val DEFAULT_BACKEND_URL = "http://localhost:8080"

private val SECURITY_CLIENT = named("security")

val securityFrontendModule = module {
    single(SECURITY_CLIENT) { HttpClient(CIO) { expectSuccess = false } } onClose { it?.close() }
    single<SecurityApi> { HttpSecurityApi(get(SECURITY_CLIENT), backendUrl()) }
    single<SessionStore> { KSafeSessionStore() }
    single {
        val client = HttpClient(CIO) {
            installBackend(backendUrl())
            installApplicationAuthentication(get(), get()) {}
        }
        AuthenticatedBackend(client, backendUrl())
    } onClose { it?.httpClient?.close() }
    viewModelOf(::AuthenticationViewModel)
}

private fun backendUrl() = System.getenv(BACKEND_URL) ?: System.getProperty(PACKAGED_BACKEND_URL) ?: DEFAULT_BACKEND_URL
