package app.roleparty.security.domain.service

import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.security.domain.model.SecurityError
import app.roleparty.security.test.SecurityBehaviorTest

class SigningOutServiceTest : SecurityBehaviorTest() {

    private val signOut = fixtures.signOut
    private val email = EmailAddress.from("gimli@roleparty.app")
    private val password = "a-valid-password"

    init {
        Given("a user signed in on two devices") {
            val refreshToken = fixtures.`given a signed in user`(email, password)
            val otherDevice = fixtures.`given a new session`(email, password)

            When("signing out of one") {
                signOut.fromDevice(refreshToken)

                Then("it should be signed out, even when replaying its token right after") {
                    fixtures.refreshAuthentication(refreshToken) shouldBeFailure SecurityError.InvalidRefreshToken
                }

                Then("the other device should stay signed in") {
                    fixtures.refreshAuthentication(otherDevice).shouldBeSuccess()
                }
            }
        }

        Given("a user signed in on two devices signing out everywhere") {
            val refreshToken = fixtures.`given a signed in user`(email, password)
            val otherDevice = fixtures.`given a new session`(email, password)

            When("signing out everywhere") {
                signOut.everywhere(fixtures.accountOf(email))

                Then("both should be signed out") {
                    fixtures.refreshAuthentication(refreshToken) shouldBeFailure SecurityError.InvalidRefreshToken
                    fixtures.refreshAuthentication(otherDevice) shouldBeFailure SecurityError.InvalidRefreshToken
                }
            }
        }
    }
}
