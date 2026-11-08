package app.roleparty.security.test

import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.test.FixturedBehaviorTest
import app.roleparty.security.domain.model.EmailCode
import app.roleparty.security.domain.model.EmailCodePurpose
import app.roleparty.security.domain.model.PasswordHash
import app.roleparty.security.domain.model.SecretHash
import app.roleparty.security.domain.port.secondary.EmailCodes
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

abstract class EmailCodesContractTest(createEmailCodes: () -> EmailCodes) : FixturedBehaviorTest() {

    private val emailCodes = createEmailCodes()

    private val email = EmailAddress.from("gimli@roleparty.app")
    private val otherEmail = EmailAddress.from("legolas@roleparty.app")
    private val hash = PasswordHash.from("hash")
    private val now = Instant.parse("2026-11-06T12:00:00Z")

    private fun emailCode(secret: String, purpose: EmailCodePurpose = EmailCodePurpose.SIGN_UP, emailAddress: EmailAddress = email) =
        EmailCode(SecretHash.of(secret), purpose, emailAddress, hash.takeIf { purpose == EmailCodePurpose.SIGN_UP }, now + 1.hours)

    init {
        Given("email codes") {
            val signUp = emailCode("sign-up-code")
            val reset = emailCode("reset-code", EmailCodePurpose.PASSWORD_RESET)
            val other = emailCode("other-code", emailAddress = otherEmail)
            listOf(signUp, reset, other).forEach { emailCodes.save(it) }

            When("finding a code") {
                Then("it should match its hash, purpose and email address") {
                    emailCodes.find(signUp.hash, EmailCodePurpose.SIGN_UP, email) shouldBe signUp
                    emailCodes.find(reset.hash, EmailCodePurpose.PASSWORD_RESET, email) shouldBe reset
                    emailCodes.find(signUp.hash, EmailCodePurpose.PASSWORD_RESET, email).shouldBeNull()
                    emailCodes.find(signUp.hash, EmailCodePurpose.SIGN_UP, otherEmail).shouldBeNull()
                }
            }

            When("deleting the codes of a purpose and an address") {
                emailCodes.deleteAll(EmailCodePurpose.SIGN_UP, email)

                Then("the others should remain") {
                    emailCodes.find(signUp.hash, EmailCodePurpose.SIGN_UP, email).shouldBeNull()
                    emailCodes.find(reset.hash, EmailCodePurpose.PASSWORD_RESET, email) shouldBe reset
                    emailCodes.find(other.hash, EmailCodePurpose.SIGN_UP, otherEmail) shouldBe other
                }
            }
        }

        Given("an expired and a valid email code") {
            val expired = emailCode("expired").copy(expiration = now - 1.hours)
            val valid = emailCode("valid")
            emailCodes.save(expired)
            emailCodes.save(valid)

            When("deleting expired codes") {
                emailCodes.deleteExpired(now)

                Then("only the expired one should be deleted") {
                    emailCodes.find(expired.hash, EmailCodePurpose.SIGN_UP, email).shouldBeNull()
                    emailCodes.find(valid.hash, EmailCodePurpose.SIGN_UP, email) shouldBe valid
                }
            }
        }
    }
}
