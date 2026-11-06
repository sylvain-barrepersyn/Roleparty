package app.roleparty.account.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.account.domain.port.primary.AccountOverviewQuery
import app.roleparty.account.domain.port.primary.AccountOverviewQuery.Filter
import app.roleparty.account.test.AccountBehaviorTest
import app.roleparty.communication.commons.EmailAddress
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class AccountOverviewQueryServiceTest : AccountBehaviorTest() {

    private val overviews = fixtures.accountOverviewQueryHandler

    private val firstEmail = EmailAddress.from("first@roleparty.app")
    private val secondEmail = EmailAddress.from("second@roleparty.app")

    init {
        Given("a registered account") {
            val accountId = fixtures.`given an account`(firstEmail)

            When("querying by account id") {
                val overview = overviews.handle(AccountOverviewQuery(Filter.ByAccountId(accountId)))

                Then("it should find the account") {
                    overview.shouldNotBeNull().emailAddress shouldBe firstEmail
                }
            }

            When("querying by email address") {
                val overview = overviews.handle(AccountOverviewQuery(Filter.ByEmailAddress(firstEmail)))

                Then("it should find the account") {
                    overview.shouldNotBeNull().accountId shouldBe accountId
                }
            }
        }

        Given("an unknown account") {
            When("querying by account id") {
                val overview = overviews.handle(AccountOverviewQuery(Filter.ByAccountId(AccountId.generate())))

                Then("it should find nothing") {
                    overview.shouldBeNull()
                }
            }

            When("querying by email address") {
                val overview = overviews.handle(AccountOverviewQuery(Filter.ByEmailAddress(firstEmail)))

                Then("it should find nothing") {
                    overview.shouldBeNull()
                }
            }
        }

        Given("an account that changed its email address") {
            val accountId = fixtures.`given an account`(firstEmail)
            fixtures.`given an email address change`(accountId, secondEmail)

            When("querying by its previous email address") {
                val overview = overviews.handle(AccountOverviewQuery(Filter.ByEmailAddress(firstEmail)))

                Then("it should find nothing") {
                    overview.shouldBeNull()
                }
            }

            When("querying by its new email address") {
                val overview = overviews.handle(AccountOverviewQuery(Filter.ByEmailAddress(secondEmail)))

                Then("it should find the account") {
                    overview.shouldNotBeNull().accountId shouldBe accountId
                }
            }
        }
    }
}
