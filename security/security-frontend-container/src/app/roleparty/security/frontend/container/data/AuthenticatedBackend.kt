package app.roleparty.security.frontend.container.data

import io.ktor.client.HttpClient

class AuthenticatedBackend(val httpClient: HttpClient, val baseUrl: String)
