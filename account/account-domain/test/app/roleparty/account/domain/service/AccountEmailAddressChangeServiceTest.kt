package app.roleparty.account.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.account.domain.model.AccountError
import app.roleparty.account.domain.model.AccountEvent.Payload
import app.roleparty.account.domain.port.primary.AccountEmailAddressChangeCommand
import app.roleparty.account.test.AccountBehaviorTest
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.hours

class AccountEmailAddressChangeServiceTest : AccountBehaviorTest() {

    private val emailAddressChange = fixtures.accountEmailAddressChangeCommandHandler

    private val firstEmail = EmailAddress.from("first@roleparty.app")
    private val secondEmail = EmailAddress.from("second@roleparty.app")

    init {
        Given("a registered account and an unused email address") {
            val accountId = fixtures.`given an account`(firstEmail)

            When("changing to this email address") {
                val outcome = emailAddressChange.handle(accountId = accountId, command = AccountEmailAddressChangeCommand(secondEmail))

                Then("it should succeed") {
                    outcome.shouldBeSuccess()
                }

                Then("the account should use the new email address") {
                    fixtures.overviewOf(accountId).emailAddress shouldBe secondEmail
                }
            }
        }

        Given("a registered account and a clock moved forward") {
            val accountId = fixtures.`given an account`(firstEmail)
            val registeredAt = clock.now()
            clock.advanceBy(1.hours)

            When("changing the email address") {
                emailAddressChange.handle(accountId = accountId, command = AccountEmailAddressChangeCommand(secondEmail)).shouldBeSuccess()

                Then("the event should occur at the new instant") {
                    fixtures.eventsOf(accountId).last().occurredAt shouldBe registeredAt + 1.hours
                }
            }
        }

        Given("a registered account") {
            val accountId = fixtures.`given an account`(firstEmail)

            When("changing to its current email address") {
                val outcome = emailAddressChange.handle(accountId = accountId, command = AccountEmailAddressChangeCommand(firstEmail))

                Then("it should succeed") {
                    outcome.shouldBeSuccess()
                }

                Then("no event should be appended") {
                    fixtures.eventsOf(accountId) shouldHaveSize 1
                }
            }
        }

        Given("two registered accounts") {
            val accountId = fixtures.`given an account`(firstEmail)
            fixtures.`given an account`(secondEmail)

            When("one account changes to the email address of the other") {
                val outcome = emailAddressChange.handle(accountId = accountId, command = AccountEmailAddressChangeCommand(secondEmail))

                Then("it should fail") {
                    outcome shouldBeFailure AccountError.EmailAddressAlreadyUsed(secondEmail)
                }
            }
        }

        Given("an unknown account") {
            val unknownId = AccountId.generate()

            When("changing the email address") {
                val outcome = emailAddressChange.handle(accountId = unknownId, command = AccountEmailAddressChangeCommand(firstEmail))

                Then("it should fail") {
                    outcome shouldBeFailure AccountError.AccountNotFound(unknownId)
                }
            }
        }

        Given("two registered accounts and an email check missing concurrent writes") {
            val accountId = fixtures.`given an account`(firstEmail)
            fixtures.`given an account`(secondEmail)
            fixtures.`given an email check missing concurrent writes`()

            When("one account changes to the email address of the other") {
                val outcome = emailAddressChange.handle(accountId = accountId, command = AccountEmailAddressChangeCommand(secondEmail))

                Then("the store should reject it") {
                    outcome shouldBeFailure AccountError.EmailAddressAlreadyUsed(secondEmail)
                }

                Then("the account should keep its email address") {
                    fixtures.overviewOf(accountId).emailAddress shouldBe firstEmail
                }
            }
        }

        Given("a registered account and a concurrent write") {
            val accountId = fixtures.`given an account`(firstEmail)
            fixtures.`given concurrent writes`(Payload.RoleChanged(AccountRole.ADMIN))

            When("changing the email address") {
                val outcome = emailAddressChange.handle(accountId = accountId, command = AccountEmailAddressChangeCommand(secondEmail))

                Then("it should succeed after retrying") {
                    outcome.shouldBeSuccess()
                }

                Then("the new email address should be applied") {
                    fixtures.overviewOf(accountId).emailAddress shouldBe secondEmail
                }

                Then("the concurrent write should be kept") {
                    fixtures.overviewOf(accountId).isAdmin shouldBe true
                }
            }
        }

        Given("a registered account and a concurrent write of the same email address") {
            val accountId = fixtures.`given an account`(firstEmail)
            fixtures.`given concurrent writes`(Payload.EmailAddressChanged(secondEmail))

            When("changing to this email address") {
                val outcome = emailAddressChange.handle(accountId = accountId, command = AccountEmailAddressChangeCommand(secondEmail))

                Then("it should succeed") {
                    outcome.shouldBeSuccess()
                }

                Then("nothing more should be appended") {
                    fixtures.eventsOf(accountId) shouldHaveSize 2
                }
            }
        }

        Given("a registered account and concurrent writes on every attempt") {
            val accountId = fixtures.`given an account`(firstEmail)
            fixtures.`given concurrent writes`(
                Payload.RoleChanged(AccountRole.ADMIN),
                Payload.RoleChanged(AccountRole.USER),
                Payload.RoleChanged(AccountRole.ADMIN),
            )

            When("changing the email address") {
                val outcome = emailAddressChange.handle(accountId = accountId, command = AccountEmailAddressChangeCommand(secondEmail))

                Then("it should fail") {
                    outcome shouldBeFailure AccountError.ConcurrentModification(accountId)
                }

                Then("the account should keep its email address") {
                    fixtures.overviewOf(accountId).emailAddress shouldBe firstEmail
                }
            }
        }
    }
}
