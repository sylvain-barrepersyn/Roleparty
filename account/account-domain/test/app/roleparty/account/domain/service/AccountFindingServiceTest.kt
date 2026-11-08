package app.roleparty.account.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.account.test.AccountBehaviorTest
import app.roleparty.communication.commons.EmailAddress
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class AccountFindingServiceTest : AccountBehaviorTest() {

    private val findAccount = fixtures.findAccount
    private val email = EmailAddress.from("gimli@roleparty.app")

    init {
        Given("an unknown account") {
            val accountId = AccountId.generate()

            When("finding it") {
                Then("it should find nothing") {
                    findAccount.byId(accountId).shouldBeNull()
                    findAccount.byEmailAddress(email).shouldBeNull()
                }
            }
        }

        Given("an account to find") {
            val accountId = fixtures.`given an account`(email)

            When("finding it") {
                Then("it should find it by id and by email address") {
                    findAccount.byId(accountId) shouldBe fixtures.accountOf(accountId)
                    findAccount.byEmailAddress(email) shouldBe fixtures.accountOf(accountId)
                }
            }
        }
    }
}
