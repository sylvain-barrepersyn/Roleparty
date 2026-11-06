package app.roleparty.account.infrastructure

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.account.domain.model.Account
import app.roleparty.account.domain.model.AccountError.EmailAddressAlreadyUsed
import app.roleparty.account.infrastructure.adapter.secondary.InfrastructuredAccounts
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.Outcome
import app.roleparty.core.test.FixturedBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlin.time.Clock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

private const val CONCURRENT_WRITES = 8

class InfrastructuredAccountsTest : FixturedBehaviorTest() {

    private val accounts = InfrastructuredAccounts(TestPostgres.postgres.database, Clock.System)
    private val email = EmailAddress.from("gimli@roleparty.app")
    private val otherEmail = EmailAddress.from("legolas@roleparty.app")

    private fun account(emailAddress: EmailAddress = email) = Account(AccountId.generate(), emailAddress, AccountRole.USER)

    private suspend fun givenAccount(emailAddress: EmailAddress = email) =
        account(emailAddress).also { accounts.register(it).shouldBeSuccess() }

    private suspend fun concurrently(write: suspend () -> Outcome<Unit, EmailAddressAlreadyUsed>) = coroutineScope {
        List(CONCURRENT_WRITES) { async(Dispatchers.IO) { write() } }.awaitAll()
    }

    private fun outbox() = TestPostgres.sql("select exchange, payload->>'accountId' from technical.event order by position")

    init {
        runBlocking { TestPostgres.clear() }

        Given("an account going through every change") {
            val account = givenAccount()
            accounts.changeEmailAddress(account.id, otherEmail).shouldBeSuccess()
            accounts.changeRole(account.id, AccountRole.ADMIN)

            When("looking at the outbox") {
                Then("it should contain a public event per change, in order") {
                    outbox() shouldBe listOf(
                        "account.account-registered|${account.id}",
                        "account.account-email-address-changed|${account.id}",
                        "account.account-role-changed|${account.id}",
                    )
                }
            }
        }

        Given("an unknown account") {
            val accountId = AccountId.generate()

            When("changing it") {
                accounts.changeEmailAddress(accountId, email).shouldBeSuccess()
                accounts.changeRole(accountId, AccountRole.ADMIN)

                Then("no event should be published") {
                    outbox().shouldBeEmpty()
                }
            }
        }

        Given("a registration rejected for its email address") {
            givenAccount()
            val eventsBefore = outbox()
            val other = account()
            val outcome = accounts.register(other)

            When("looking at what was stored") {
                Then("it should have been rejected") {
                    outcome shouldBeFailure EmailAddressAlreadyUsed(email)
                }

                Then("the outbox should not change") {
                    outbox() shouldBe eventsBefore
                }
            }
        }

        Given("an email address change rejected for its email address") {
            val account = givenAccount()
            givenAccount(otherEmail)
            val eventsBefore = outbox()
            val outcome = accounts.changeEmailAddress(account.id, otherEmail)

            When("looking at what was stored") {
                Then("it should have been rejected") {
                    outcome shouldBeFailure EmailAddressAlreadyUsed(otherEmail)
                }

                Then("the outbox should not change") {
                    outbox() shouldBe eventsBefore
                }
            }
        }

        Given("concurrent registrations with the same email address") {
            When("they all run") {
                val outcomes = concurrently { accounts.register(account()) }

                Then("only one should succeed and the others should report the email address as used") {
                    outcomes.count { it is Outcome.Success } shouldBe 1
                    outcomes.filterIsInstance<Outcome.Failure<EmailAddressAlreadyUsed>>().map { it.error }.toSet() shouldBe
                        setOf(EmailAddressAlreadyUsed(email))
                }

                Then("only the winner should be stored") {
                    TestPostgres.sql("select count(*) from account.account") shouldBe listOf("1")
                    outbox().size shouldBe 1
                }
            }
        }

        Given("accounts concurrently changing to the same email address") {
            val contenders = List(CONCURRENT_WRITES) { givenAccount(EmailAddress.from("contender-$it@roleparty.app")) }

            When("they all run") {
                val outcomes = coroutineScope {
                    contenders.map { async(Dispatchers.IO) { accounts.changeEmailAddress(it.id, email) } }.awaitAll()
                }

                Then("only one should succeed") {
                    outcomes.count { it is Outcome.Success } shouldBe 1
                }

                Then("only one should hold it") {
                    TestPostgres.sql("select count(*) from account.account where email_address = '$email'") shouldBe listOf("1")
                }
            }
        }
    }
}
