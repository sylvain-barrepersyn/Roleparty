package app.roleparty.account.domain.model

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.account.domain.model.AccountEvent.Payload
import app.roleparty.communication.commons.EmailAddress
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlin.time.Instant

class AccountDeciderTest : BehaviorSpec({

    val accountId = AccountId.generate()
    val initialEmail = EmailAddress.from("initial@roleparty.app")
    val newEmail = EmailAddress.from("new@roleparty.app")

    fun event(payload: Payload, millis: Long = 0) = AccountEvent(
        id = AccountEvent.Id.generate(),
        accountId = accountId,
        occurredAt = Instant.fromEpochMilliseconds(millis),
        payload = payload,
    )

    Given("a registration command") {
        val command = AccountCreationCommand.Register(emailAddress = initialEmail)

        When("deciding") {
            val payloads = AccountDecider.decide(command)

            Then("the account should be registered as a simple user") {
                payloads shouldBe listOf(Payload.Registered(initialEmail, AccountRole.USER))
            }
        }
    }

    Given("an event stream containing a duplicate registration") {
        val events = listOf(
            event(Payload.Registered(initialEmail, AccountRole.USER), millis = 0),
            event(Payload.Registered(newEmail, AccountRole.USER), millis = 1),
        )

        When("hydrating the decider") {
            Then("it should fail") {
                shouldThrow<IllegalStateException> { AccountDecider.from(events) }
            }
        }
    }

    Given("an event stream that does not start with a registration") {
        val events = listOf(event(Payload.EmailAddressChanged(newEmail)))

        When("hydrating the decider") {
            Then("it should fail") {
                shouldThrow<IllegalStateException> { AccountDecider.from(events) }
            }
        }
    }

    Given("a registered simple user") {
        val decider = AccountDecider.from(listOf(event(Payload.Registered(initialEmail, AccountRole.USER))))

        When("changing the email address to a different one") {
            val payloads = decider.decide(AccountLifecycleCommand.ChangeEmailAddress(emailAddress = newEmail))

            Then("the email address should change") {
                payloads shouldBe listOf(Payload.EmailAddressChanged(newEmail))
            }
        }

        When("changing the email address to the current one") {
            val payloads = decider.decide(AccountLifecycleCommand.ChangeEmailAddress(emailAddress = initialEmail))

            Then("no event should be emitted") {
                payloads.shouldBeEmpty()
            }
        }

        When("promoting the user to admin") {
            val payloads = decider.decide(AccountLifecycleCommand.ChangeRole(role = AccountRole.ADMIN))

            Then("the role should change") {
                payloads shouldBe listOf(Payload.RoleChanged(AccountRole.ADMIN))
            }
        }
    }

    Given("a registered admin") {
        val decider = AccountDecider.from(
            listOf(
                event(Payload.Registered(initialEmail, AccountRole.USER), millis = 0),
                event(Payload.RoleChanged(AccountRole.ADMIN), millis = 1),
            )
        )

        When("changing the role to the current one") {
            val payloads = decider.decide(AccountLifecycleCommand.ChangeRole(role = AccountRole.ADMIN))

            Then("no event should be emitted") {
                payloads.shouldBeEmpty()
            }
        }
    }
})
