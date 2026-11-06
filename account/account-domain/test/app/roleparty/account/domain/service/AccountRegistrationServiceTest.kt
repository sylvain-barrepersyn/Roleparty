package app.roleparty.account.domain.service

import app.roleparty.account.commons.AccountRole
import app.roleparty.account.domain.model.AccountError
import app.roleparty.account.domain.model.AccountEvent.Payload
import app.roleparty.account.domain.port.primary.AccountRegistrationCommand
import app.roleparty.account.test.AccountBehaviorTest
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.shouldBe
import kotlin.time.Instant

class AccountRegistrationServiceTest : AccountBehaviorTest() {

    private val registration = fixtures.accountRegistrationCommandHandler

    private val firstEmail = EmailAddress.from("first@roleparty.app")
    private val secondEmail = EmailAddress.from("second@roleparty.app")

    init {
        Given("an unused email address") {
            When("registering with it") {
                val accountId = registration.handle(AccountRegistrationCommand(firstEmail)).shouldBeSuccess()

                Then("the account should use this email address") {
                    fixtures.overviewOf(accountId).emailAddress shouldBe firstEmail
                }

                Then("the account should be a simple user") {
                    fixtures.overviewOf(accountId).role shouldBe AccountRole.USER
                }
            }
        }

        Given("a clock set to a given instant") {
            val instant = Instant.parse("2026-01-01T00:00:00Z")
            clock.setTo(instant)

            When("registering") {
                val accountId = registration.handle(AccountRegistrationCommand(firstEmail)).shouldBeSuccess()

                Then("the event should occur at this instant") {
                    fixtures.eventsOf(accountId).shouldBeSingleton { it.occurredAt shouldBe instant }
                }
            }
        }

        Given("an email address used by an account") {
            fixtures.`given an account`(firstEmail)

            When("registering with it") {
                val outcome = registration.handle(AccountRegistrationCommand(firstEmail))

                Then("it should fail") {
                    outcome shouldBeFailure AccountError.EmailAddressAlreadyUsed(firstEmail)
                }
            }
        }

        Given("an email address used by an account and an email check missing concurrent writes") {
            fixtures.`given an account`(firstEmail)
            fixtures.`given an email check missing concurrent writes`()

            When("registering with it") {
                val outcome = registration.handle(AccountRegistrationCommand(firstEmail))

                Then("the store should reject it") {
                    outcome shouldBeFailure AccountError.EmailAddressAlreadyUsed(firstEmail)
                }
            }
        }

        Given("an email address released by an account") {
            val accountId = fixtures.`given an account`(firstEmail)
            fixtures.`given an email address change`(accountId, secondEmail)

            When("registering with it") {
                val outcome = registration.handle(AccountRegistrationCommand(firstEmail))

                Then("it should succeed") {
                    outcome.shouldBeSuccess()
                }
            }
        }

        Given("a concurrent write on the generated account") {
            fixtures.`given concurrent writes`(Payload.Registered(secondEmail, AccountRole.USER))

            When("registering") {
                Then("it should be treated as a bug") {
                    shouldThrow<IllegalStateException> { registration.handle(AccountRegistrationCommand(firstEmail)) }
                }
            }
        }
    }
}
