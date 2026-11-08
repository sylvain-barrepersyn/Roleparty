package app.roleparty.security.infrastructure

import app.roleparty.account.commons.AccountId
import app.roleparty.security.domain.model.DeviceId
import app.roleparty.security.domain.model.SecretHash
import app.roleparty.security.domain.model.StoredRefreshToken
import app.roleparty.security.infrastructure.adapter.secondary.InfrastructuredRefreshTokens
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class InfrastructuredRefreshTokensTest : FunSpec({

    test("only one concurrent rotation of a token should count") {
        val refreshTokens = InfrastructuredRefreshTokens(TestPostgres.database)
        val now = Instant.parse("2026-11-06T12:00:00Z")
        val hash = SecretHash.of("a-secret-for-concurrent-rotations")
        refreshTokens.save(StoredRefreshToken(hash, AccountId.generate(), DeviceId.generate(), now + 1.hours, rotatedAt = null, revoked = false))

        val rotations = coroutineScope {
            List(8) { async(Dispatchers.IO) { refreshTokens.markRotated(hash, now) } }.awaitAll()
        }

        rotations.count { it } shouldBe 1
    }
})
