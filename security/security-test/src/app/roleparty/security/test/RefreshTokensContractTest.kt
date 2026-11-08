package app.roleparty.security.test

import app.roleparty.account.commons.AccountId
import app.roleparty.core.test.FixturedBehaviorTest
import app.roleparty.security.domain.model.DeviceId
import app.roleparty.security.domain.model.SecretHash
import app.roleparty.security.domain.model.StoredRefreshToken
import app.roleparty.security.domain.port.secondary.RefreshTokens
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

abstract class RefreshTokensContractTest(createRefreshTokens: () -> RefreshTokens) : FixturedBehaviorTest() {

    private val refreshTokens = createRefreshTokens()

    private val now = Instant.parse("2026-11-06T12:00:00Z")

    private fun refreshToken(
        secret: String,
        accountId: AccountId = AccountId.generate(),
        deviceId: DeviceId = DeviceId.generate(),
    ) = StoredRefreshToken(SecretHash.of(secret), accountId, deviceId, now + 1.hours, rotatedAt = null, revoked = false)

    init {
        Given("a refresh token") {
            val token = refreshToken("a-secret")
            refreshTokens.save(token)

            When("finding it") {
                Then("it should be found with its hash only") {
                    refreshTokens.find(token.hash) shouldBe token
                    refreshTokens.find(SecretHash.of("another-secret")).shouldBeNull()
                }
            }
        }

        Given("a refresh token rotated twice") {
            val token = refreshToken("a-secret")
            refreshTokens.save(token)

            When("rotating it") {
                val first = refreshTokens.markRotated(token.hash, now)
                val second = refreshTokens.markRotated(token.hash, now + 1.hours)

                Then("only the first rotation should count") {
                    first shouldBe true
                    second shouldBe false
                    refreshTokens.find(token.hash)?.rotatedAt shouldBe now
                }
            }
        }

        Given("tokens of two devices and of another account") {
            val accountId = AccountId.generate()
            val deviceId = DeviceId.generate()
            val firstOfDevice = refreshToken("first", accountId, deviceId)
            val secondOfDevice = refreshToken("second", accountId, deviceId)
            val otherDevice = refreshToken("other-device", accountId)
            val otherAccount = refreshToken("other-account")
            listOf(firstOfDevice, secondOfDevice, otherDevice, otherAccount).forEach { refreshTokens.save(it) }

            When("revoking the device") {
                refreshTokens.revokeDevice(accountId, deviceId)

                Then("only the tokens of the device should be revoked") {
                    refreshTokens.find(firstOfDevice.hash)?.revoked shouldBe true
                    refreshTokens.find(secondOfDevice.hash)?.revoked shouldBe true
                    refreshTokens.find(otherDevice.hash)?.revoked shouldBe false
                }
            }

            When("revoking the whole account") {
                refreshTokens.revokeAllFor(accountId)

                Then("all its tokens should be revoked, and only its tokens") {
                    refreshTokens.find(otherDevice.hash)?.revoked shouldBe true
                    refreshTokens.find(otherAccount.hash)?.revoked shouldBe false
                }
            }
        }

        Given("an expired and a valid refresh token") {
            val expired = refreshToken("expired").copy(expiration = now - 1.hours)
            val valid = refreshToken("valid")
            refreshTokens.save(expired)
            refreshTokens.save(valid)

            When("deleting expired tokens") {
                refreshTokens.deleteExpired(now)

                Then("only the expired one should be deleted") {
                    refreshTokens.find(expired.hash).shouldBeNull()
                    refreshTokens.find(valid.hash) shouldBe valid
                }
            }
        }
    }
}
