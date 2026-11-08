package app.roleparty.campaign.creature.domain.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class DiceExpressionTest : FunSpec({

    test("an expression should be written and read back") {
        listOf(DiceExpression(4, 8, 5), DiceExpression(2, 6, -1), DiceExpression(1, 20)).forEach {
            DiceExpression.parse(it.toString()) shouldBe it
        }
        DiceExpression(4, 8, 5).toString() shouldBe "4d8+5"
        DiceExpression.parse(" 3 d 6 - 2 ") shouldBe DiceExpression(3, 6, -2)
    }

    test("something else should not be read") {
        DiceExpression.parse("d8").shouldBeNull()
        DiceExpression.parse("four dice").shouldBeNull()
    }
})
