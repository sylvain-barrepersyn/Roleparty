package app.roleparty.security.domain.service

import app.roleparty.security.domain.model.PasswordHashingCost
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldStartWith

class PasswordHashingServiceTest : FunSpec({
    val hasher = PasswordHashingService(PasswordHashingCost())

    test("a password should be verified against its hash, and salted") {
        val hash = hasher.hash("a-valid-password")

        hash.toString() shouldStartWith "\$argon2id\$v=19\$m=65536,t=3,p=1\$"
        hash shouldNotBe hasher.hash("a-valid-password")
        hasher.verify("a-valid-password", hash) shouldBe true
        hasher.verify("wrong-password", hash) shouldBe false
    }
})
