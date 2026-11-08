package app.roleparty.security.infrastructure

import app.roleparty.communication.commons.EmailSender
import app.roleparty.core.commons.requireValueOrFile
import app.roleparty.core.database.Postgres
import app.roleparty.core.ktor.rpc.rpcConnection
import app.roleparty.core.rabbitmq.RabbitMq
import app.roleparty.core.security.AccessTokenKeys
import app.roleparty.core.security.installAuthentication
import app.roleparty.security.database.setUpSecuritySchema
import app.roleparty.security.domain.SecurityModule
import app.roleparty.security.domain.port.primary.PurgeExpiredSecrets
import app.roleparty.security.infrastructure.adapter.primary.AccountEmailAddressChangedRabbitMqHandler
import app.roleparty.security.infrastructure.adapter.primary.SecurityLocalization
import app.roleparty.security.infrastructure.adapter.primary.securityRoutes
import app.roleparty.security.infrastructure.adapter.secondary.InfrastructuredAccounts
import app.roleparty.security.infrastructure.adapter.secondary.InfrastructuredCredentials
import app.roleparty.security.infrastructure.adapter.secondary.InfrastructuredEmailCodes
import app.roleparty.security.infrastructure.adapter.secondary.InfrastructuredRefreshTokens
import app.roleparty.security.infrastructure.adapter.secondary.InfrastructuredSecurityEmails
import io.ktor.events.EventDefinition
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopping
import io.ktor.server.application.ServerReady
import io.ktor.server.application.install
import io.ktor.server.plugins.origin
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.plugins.ratelimit.RateLimitName
import app.roleparty.core.ktor.rest.resourceRouting
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val PUBLIC_KEY = "JWT_PUBLIC_KEY"
private const val PRIVATE_KEY = "JWT_PRIVATE_KEY"
private const val ACCOUNT_RPC_URL = "ACCOUNT_RPC_URL"
private const val ACCOUNT_RPC_TOKEN = "ACCOUNT_RPC_TOKEN"
private const val CREDENTIALS_REQUESTS_PER_MINUTE = 10
private const val SESSIONS_REQUESTS_PER_MINUTE = 60

private val CREDENTIALS_RATE_LIMIT = RateLimitName("security-credentials")
private val SESSIONS_RATE_LIMIT = RateLimitName("security-sessions")
private val PURGE_INTERVAL = 1.hours

fun Application.loadSecurity(
    postgres: Postgres,
    rabbitMq: RabbitMq,
    emailSender: EmailSender,
    localization: SecurityLocalization,
    env: Map<String, String> = System.getenv(),
    clock: Clock = Clock.System,
): SecurityModule {
    postgres.setUpSecuritySchema()

    val accounts = InfrastructuredAccounts(
        rpcConnection(env.requireValueOrFile(ACCOUNT_RPC_URL), env.requireValueOrFile(ACCOUNT_RPC_TOKEN))
    )

    val security = SecurityModule(
        accounts = accounts,
        credentials = InfrastructuredCredentials(postgres.database),
        refreshTokens = InfrastructuredRefreshTokens(postgres.database),
        emailCodes = InfrastructuredEmailCodes(postgres.database),
        emails = InfrastructuredSecurityEmails(this, emailSender, environment.log),
        accessTokenPublicKey = AccessTokenKeys.publicKey(env.requireValueOrFile(PUBLIC_KEY)),
        accessTokenPrivateKey = AccessTokenKeys.privateKey(env.requireValueOrFile(PRIVATE_KEY)),
        clock = clock,
    )

    installAuthentication(env)

    install(RateLimit) {
        register(CREDENTIALS_RATE_LIMIT) {
            rateLimiter(limit = CREDENTIALS_REQUESTS_PER_MINUTE, refillPeriod = 1.minutes)
            requestKey { call -> call.request.origin.remoteHost }
        }
        register(SESSIONS_RATE_LIMIT) {
            rateLimiter(limit = SESSIONS_REQUESTS_PER_MINUTE, refillPeriod = 1.minutes)
            requestKey { call -> call.request.origin.remoteHost }
        }
    }

    resourceRouting {
        securityRoutes(security, localization, CREDENTIALS_RATE_LIMIT, SESSIONS_RATE_LIMIT)
    }

    rabbitMq.registerEventHandler(AccountEmailAddressChangedRabbitMqHandler(security.changeCredentialEmailAddress))

    checkAccountRpcOnStart(accounts)
    purgeExpiredPeriodically(security.purgeExpiredSecrets)

    return security
}

private fun Application.checkAccountRpcOnStart(accounts: InfrastructuredAccounts) {
    val onReady: (Any) -> Unit = {
        launch {
            try {
                accounts.ping()
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                environment.log.error("Account RPC is unreachable: sign ups can't be verified. Check $ACCOUNT_RPC_URL and $ACCOUNT_RPC_TOKEN", exception)
            }
        }
    }
    @Suppress("UNCHECKED_CAST")
    monitor.subscribe(ServerReady as EventDefinition<Any>, onReady)
}

private fun Application.purgeExpiredPeriodically(purgeExpiredSecrets: PurgeExpiredSecrets) {
    val job = launch {
        while (isActive) {
            try {
                purgeExpiredSecrets()
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                environment.log.warn("Impossible to purge expired security data: ${exception.message}")
            }
            delay(PURGE_INTERVAL)
        }
    }
    monitor.subscribe(ApplicationStopping) { job.cancel() }
}
