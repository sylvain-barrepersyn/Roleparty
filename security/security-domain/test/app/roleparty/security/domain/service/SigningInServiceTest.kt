package app.roleparty.security.domain.service

import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.security.domain.model.SecurityError
import app.roleparty.security.test.SecurityBehaviorTest
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotContain
import kotlin.time.Duration.Companion.minutes

class SigningInServiceTest : SecurityBehaviorTest() {

    private val signIn = fixtures.signIn
    private val email = EmailAddress.from("gimli@roleparty.app")
    private val password = "a-valid-password"

    init {
        Given("a user signing in") {
            fixtures.`given a verified user`(email, password)

            When("using their password") {
                val authentication = signIn(email, password).shouldBeSuccess()

                Then("it should issue tokens for their account") {
                    val accessToken = JWT.decode(authentication.accessToken.value.toString())
                    Algorithm.ECDSA256(fixtures.accessTokenPublicKey, null).verify(accessToken)
                    accessToken.subject shouldBe fixtures.accountOf(email).toString()
                }

                Then("it should only store a hash of the refresh token") {
                    val stored = fixtures.refreshTokens().single()
                    stored.revoked shouldBe false
                    stored.hash.toString() shouldNotBe authentication.refreshToken.value.toString()
                    stored.hash.toString() shouldNotContain authentication.refreshToken.value.toString()
                }
            }
        }

        Given("a user and a wrong password") {
            fixtures.`given a verified user`(email, password)

            When("signing in with it") {
                val outcome = signIn(email, "wrong-password")

                Then("it should fail") {
                    outcome shouldBeFailure SecurityError.InvalidCredentials
                }

                Then("it should not issue any token") {
                    fixtures.refreshTokens().shouldBeEmpty()
                }
            }
        }

        Given("an unknown email address") {
            When("signing in") {
                val outcome = signIn(email, password)

                Then("it should fail like a wrong password") {
                    outcome shouldBeFailure SecurityError.InvalidCredentials
                }
            }
        }

        Given("a password far too long") {
            When("signing in with it") {
                val outcome = signIn(email, "x".repeat(129))

                Then("it should fail") {
                    outcome shouldBeFailure SecurityError.InvalidCredentials
                }
            }
        }

        Given("too many failed sign ins on an address") {
            fixtures.`given a verified user`(email, password)
            repeat(10) { signIn(email, "wrong-password") }

            When("signing in with the right password") {
                val blocked = signIn(email, password)

                Then("it should be blocked") {
                    blocked shouldBeFailure SecurityError.TooManyAttempts
                }
            }

            When("the window has passed") {
                clock.advanceBy(16.minutes)

                Then("the right password should work again") {
                    signIn(email, password).shouldBeSuccess()
                }
            }
        }

        Given("failed sign ins followed by a success") {
            fixtures.`given a verified user`(email, password)
            repeat(9) { signIn(email, "wrong-password") }
            signIn(email, password).shouldBeSuccess()

            When("failing again") {
                repeat(9) { signIn(email, "wrong-password") }

                Then("the counter should have restarted") {
                    signIn(email, password).shouldBeSuccess()
                }
            }
        }
    }
}
