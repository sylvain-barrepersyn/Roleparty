package app.roleparty.account.infrastructure

import app.roleparty.account.commons.AccountId
import app.roleparty.account.commons.AccountRole
import app.roleparty.account.domain.model.AccountEvent
import app.roleparty.account.domain.model.AccountEvent.Payload
import app.roleparty.account.domain.model.AccountVersion
import app.roleparty.account.domain.port.secondary.AccountAppendFailure
import app.roleparty.account.infrastructure.adapter.secondary.InfrastructuredAccounts
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.Outcome
import app.roleparty.core.test.FixturedBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

private const val CONCURRENT_APPENDS = 8

class InfrastructuredAccountsTest : FixturedBehaviorTest() {

    private val accounts = InfrastructuredAccounts(TestPostgres.postgres.database)
    private val email = EmailAddress.from("gimli@roleparty.app")
    private val otherEmail = EmailAddress.from("legolas@roleparty.app")

    private fun event(accountId: AccountId, payload: Payload) =
        AccountEvent(AccountEvent.Id.generate(), accountId, clock.now(), payload)

    private fun registration(accountId: AccountId, emailAddress: EmailAddress) =
        event(accountId, Payload.Registered(emailAddress, AccountRole.USER))

    private suspend fun appendConcurrently(append: suspend (Int) -> Outcome<Unit, AccountAppendFailure>) = coroutineScope {
        List(CONCURRENT_APPENDS) { index -> async(Dispatchers.IO) { append(index) } }.awaitAll()
    }

    private fun outboxRows() = TestPostgres.sql(
        "select id, exchange, payload->>'accountId' from technical.event order by position"
    )

    init {
        runBlocking { TestPostgres.clear() }

        Given("an account without events") {
            val accountId = AccountId.generate()
            val registered = registration(accountId, email)
            val changed = event(accountId, Payload.EmailAddressChanged(otherEmail))
            val promoted = event(accountId, Payload.RoleChanged(AccountRole.ADMIN))

            When("appending events") {
                accounts.append(accountId, AccountVersion.INITIAL, listOf(registered, changed, promoted)).shouldBeSuccess()

                Then("their public form should be stored in the outbox, in order and with their ids") {
                    outboxRows() shouldBe listOf(
                        "${registered.id}|account.account-registered|$accountId",
                        "${changed.id}|account.account-email-address-changed|$accountId",
                        "${promoted.id}|account.account-role-changed|$accountId",
                    )
                }

                Then("the overview should reflect the last events") {
                    TestPostgres.sql("select email_address, role from account.overview") shouldBe listOf("$otherEmail|ADMIN")
                }
            }
        }

        Given("an append rejected for its version") {
            val accountId = AccountId.generate()
            accounts.append(accountId, AccountVersion.INITIAL, listOf(registration(accountId, email))).shouldBeSuccess()
            val eventsBefore = outboxRows()
            val outcome = accounts.append(
                accountId,
                AccountVersion.INITIAL,
                listOf(event(accountId, Payload.RoleChanged(AccountRole.ADMIN))),
            )

            When("looking at the outbox") {
                Then("the append should have been rejected") {
                    outcome shouldBeFailure AccountAppendFailure.VersionConflict
                }

                Then("it should not change") {
                    outboxRows() shouldBe eventsBefore
                }
            }
        }

        Given("an append rejected for its email address") {
            val owner = AccountId.generate()
            accounts.append(owner, AccountVersion.INITIAL, listOf(registration(owner, email))).shouldBeSuccess()
            val eventsBefore = outboxRows()
            val accountId = AccountId.generate()
            val outcome = accounts.append(accountId, AccountVersion.INITIAL, listOf(registration(accountId, email)))

            When("looking at what was stored") {
                Then("the append should have been rejected") {
                    outcome shouldBeFailure AccountAppendFailure.EmailAddressAlreadyUsed(email)
                }

                Then("the events of the transaction should have been rolled back") {
                    TestPostgres.sql("select count(*) from account.event where account_id = '$accountId'") shouldBe listOf("0")
                }

                Then("the outbox should not change") {
                    outboxRows() shouldBe eventsBefore
                }
            }
        }

        Given("concurrent appends at the same version") {
            val accountId = AccountId.generate()
            accounts.append(accountId, AccountVersion.INITIAL, listOf(registration(accountId, email))).shouldBeSuccess()

            When("they all run") {
                val outcomes = appendConcurrently {
                    accounts.append(accountId, AccountVersion.from(1), listOf(event(accountId, Payload.RoleChanged(AccountRole.ADMIN))))
                }

                Then("only one should succeed and the others should report a version conflict") {
                    outcomes.count { it is Outcome.Success } shouldBe 1
                    outcomes.filterIsInstance<Outcome.Failure<AccountAppendFailure>>().map { it.error }.toSet() shouldBe
                        setOf(AccountAppendFailure.VersionConflict)
                }

                Then("the stream should only contain the winner") {
                    accounts.load(accountId).version shouldBe AccountVersion.from(2)
                }
            }
        }

        Given("concurrent registrations with the same email address") {
            When("they all run") {
                val outcomes = appendConcurrently {
                    val accountId = AccountId.generate()
                    accounts.append(accountId, AccountVersion.INITIAL, listOf(registration(accountId, email)))
                }

                Then("only one should succeed and the others should report the email address as used") {
                    outcomes.count { it is Outcome.Success } shouldBe 1
                    outcomes.filterIsInstance<Outcome.Failure<AccountAppendFailure>>().map { it.error }.toSet() shouldBe
                        setOf(AccountAppendFailure.EmailAddressAlreadyUsed(email))
                }

                Then("only the winner should be stored") {
                    TestPostgres.sql("select count(*) from account.event") shouldBe listOf("1")
                    outboxRows().size shouldBe 1
                }
            }
        }
    }
}
