package app.roleparty.security.domain.service

import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.security.domain.model.RefreshToken
import app.roleparty.security.domain.model.SecurityError
import app.roleparty.security.test.SecurityBehaviorTest
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

class AuthenticationRefreshingServiceTest : SecurityBehaviorTest() {

    private val refreshAuthentication = fixtures.refreshAuthentication
    private val email = EmailAddress.from("gimli@roleparty.app")
    private val password = "a-valid-password"

    init {
        Given("a signed in user refreshing") {
            val refreshToken = fixtures.`given a signed in user`(email, password)

            When("using their token") {
                val authentication = refreshAuthentication(refreshToken).shouldBeSuccess()

                Then("it should issue a different token on the same device") {
                    authentication.refreshToken.value shouldNotBe refreshToken
                    fixtures.refreshTokens().map { it.deviceId }.toSet() shouldHaveSize 1
                    fixtures.refreshTokens() shouldHaveSize 2
                }
            }
        }

        Given("a refresh retried just after a lost response") {
            val refreshToken = fixtures.`given a signed in user`(email, password)
            refreshAuthentication(refreshToken).shouldBeSuccess()
            clock.advanceBy(10.seconds)

            When("retrying with the same token") {
                val outcome = refreshAuthentication(refreshToken)

                Then("it should succeed without revoking the session") {
                    outcome.shouldBeSuccess()
                    fixtures.refreshTokens().none { it.revoked } shouldBe true
                }
            }
        }

        Given("a token reused long after its rotation, and another device") {
            val refreshToken = fixtures.`given a signed in user`(email, password)
            val otherDevice = fixtures.`given a new session`(email, password)
            val rotated = refreshAuthentication(refreshToken).shouldBeSuccess().refreshToken.value
            clock.advanceBy(31.seconds)

            When("reusing it") {
                val outcome = refreshAuthentication(refreshToken)

                Then("it should fail") {
                    outcome shouldBeFailure SecurityError.InvalidRefreshToken
                }

                Then("it should revoke its device, as the token was probably stolen") {
                    refreshAuthentication(rotated) shouldBeFailure SecurityError.InvalidRefreshToken
                }

                Then("the other device should stay signed in") {
                    refreshAuthentication(otherDevice).shouldBeSuccess()
                }
            }
        }

        Given("tokens that can't be used") {
            When("refreshing with an unknown token") {
                val outcome = refreshAuthentication(RefreshToken.from("garbage"))

                Then("it should fail") {
                    outcome shouldBeFailure SecurityError.InvalidRefreshToken
                }
            }
        }

        Given("an expired refresh token") {
            val refreshToken = fixtures.`given a signed in user`(email, password)
            clock.advanceBy(15.days)

            When("refreshing with it") {
                val outcome = refreshAuthentication(refreshToken)

                Then("it should fail") {
                    outcome shouldBeFailure SecurityError.InvalidRefreshToken
                }
            }
        }
    }
}
