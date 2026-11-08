package app.roleparty.security.frontend.container.data

import app.roleparty.core.commons.Outcome
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer

fun HttpClientConfig<*>.installApplicationAuthentication(
    api: SecurityApi,
    store: SessionStore,
    onSessionExpired: () -> Unit,
) {
    install(Auth) {
        bearer {
            sendWithoutRequest { true }

            loadTokens {
                store.load()?.let { BearerTokens(it.accessToken, it.refreshToken) }
            }

            refreshTokens {
                val refreshToken = store.load()?.refreshToken ?: return@refreshTokens null
                when (val outcome = api.refresh(refreshToken)) {
                    is Outcome.Success -> {
                        store.save(outcome.value)
                        BearerTokens(outcome.value.accessToken, outcome.value.refreshToken)
                    }

                    is Outcome.Failure -> {
                        if (outcome.error is SecurityFailure.Rejected) {
                            store.clear()
                            onSessionExpired()
                        }
                        null
                    }
                }
            }
        }
    }
}
