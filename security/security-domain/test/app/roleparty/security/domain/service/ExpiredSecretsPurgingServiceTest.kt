package app.roleparty.security.domain.service

import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.security.test.SecurityBehaviorTest
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import java.util.Locale
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class ExpiredSecretsPurgingServiceTest : SecurityBehaviorTest() {

    private val purgeExpiredSecrets = fixtures.purgeExpiredSecrets
    private val email = EmailAddress.from("gimli@roleparty.app")
    private val password = "a-valid-password"
    private val en = Locale.ENGLISH

    init {
        Given("a sign up whose code expired") {
            fixtures.signUp(email, password, en).shouldBeSuccess()
            clock.advanceBy(61.minutes)

            When("purging expired data") {
                purgeExpiredSecrets()

                Then("the code should be gone") {
                    fixtures.storedEmailCodes() shouldBe 0
                }
            }
        }

        Given("an expired refresh token") {
            fixtures.`given a signed in user`(email, password)
            clock.advanceBy(15.days)

            When("purging expired data") {
                purgeExpiredSecrets()

                Then("the token should be gone") {
                    fixtures.refreshTokens().shouldBeEmpty()
                }
            }
        }
    }
}
