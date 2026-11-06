package app.roleparty.core.test

import app.roleparty.core.commons.Outcome
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

fun <T> Outcome<T, *>.shouldBeSuccess(): T =
    shouldBeInstanceOf<Outcome.Success<T>>().value

infix fun <E> Outcome<*, E>.shouldBeFailure(expected: E) {
    shouldBeInstanceOf<Outcome.Failure<E>>().error shouldBe expected
}
