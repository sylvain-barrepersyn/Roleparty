package app.roleparty.security.test

import app.roleparty.account.commons.AccountId
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.test.FixturedBehaviorTest
import app.roleparty.security.domain.model.Credential
import app.roleparty.security.domain.model.PasswordHash
import app.roleparty.security.domain.port.secondary.Credentials
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

abstract class CredentialsContractTest(createCredentials: () -> Credentials) : FixturedBehaviorTest() {

    private val credentials = createCredentials()

    private val email = EmailAddress.from("gimli@roleparty.app")
    private val otherEmail = EmailAddress.from("legolas@roleparty.app")
    private val hash = PasswordHash.from("hash")
    private val otherHash = PasswordHash.from("other-hash")

    init {
        Given("no credential") {
            When("finding or changing one") {
                val accountId = AccountId.generate()
                credentials.changeEmailAddress(accountId, email)
                credentials.changePasswordHash(accountId, hash)

                Then("it should find nothing and create nothing") {
                    credentials.findByEmailAddress(email).shouldBeNull()
                    credentials.findByAccountId(accountId).shouldBeNull()
                }
            }
        }

        Given("a saved credential") {
            val accountId = AccountId.generate()
            val credential = Credential(accountId, email, hash)
            credentials.saveIfAbsent(credential)

            When("finding it") {
                Then("it should find it by email address and by account") {
                    credentials.findByEmailAddress(email) shouldBe credential
                    credentials.findByAccountId(accountId) shouldBe credential
                }
            }

            When("saving another one for the same account") {
                credentials.saveIfAbsent(Credential(accountId, otherEmail, otherHash))

                Then("it should keep the first one") {
                    credentials.findByEmailAddress(email) shouldBe credential
                    credentials.findByEmailAddress(otherEmail).shouldBeNull()
                }
            }

            When("saving another one for the same email address") {
                credentials.saveIfAbsent(Credential(AccountId.generate(), email, otherHash))

                Then("it should keep the first one") {
                    credentials.findByEmailAddress(email) shouldBe credential
                }
            }
        }

        Given("a credential that changes its email address") {
            val accountId = AccountId.generate()
            credentials.saveIfAbsent(Credential(accountId, email, hash))

            When("changing it") {
                credentials.changeEmailAddress(accountId, otherEmail)

                Then("it should only be found with the new one, keeping its password") {
                    credentials.findByEmailAddress(email).shouldBeNull()
                    credentials.findByEmailAddress(otherEmail) shouldBe Credential(accountId, otherEmail, hash)
                }
            }
        }

        Given("a credential that changes its password") {
            val accountId = AccountId.generate()
            credentials.saveIfAbsent(Credential(accountId, email, hash))

            When("changing it") {
                credentials.changePasswordHash(accountId, otherHash)

                Then("it should have the new hash") {
                    credentials.findByAccountId(accountId) shouldBe Credential(accountId, email, otherHash)
                }
            }
        }
    }
}
