package app.roleparty.core.commons

sealed interface Outcome<out T, out E> {

    data class Success<out T>(val value: T) : Outcome<T, Nothing>

    data class Failure<out E>(val error: E) : Outcome<Nothing, E>

    companion object {
        fun success() = Success(Unit)
    }
}

inline fun <T, E, R> Outcome<T, E>.map(transform: (T) -> R): Outcome<R, E> = when (this) {
    is Outcome.Success -> Outcome.Success(transform(value))
    is Outcome.Failure -> this
}

inline fun <T, E> Outcome<T, E>.getOrElse(onFailure: (E) -> T): T = when (this) {
    is Outcome.Success -> value
    is Outcome.Failure -> onFailure(error)
}
