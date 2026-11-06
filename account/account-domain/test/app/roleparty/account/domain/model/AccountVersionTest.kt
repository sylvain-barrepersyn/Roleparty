package app.roleparty.account.domain.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AccountVersionTest : FunSpec({

    test("should start at zero") {
        AccountVersion.INITIAL shouldBe AccountVersion.from(0)
    }

    test("should reject a negative version") {
        shouldThrow<IllegalArgumentException> { AccountVersion.from(-1) }
    }
})
