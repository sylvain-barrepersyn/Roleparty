package app.roleparty.communication.commons

import app.roleparty.communication.commons.CommunicationError.InvalidEmailAddress
import app.roleparty.core.commons.Outcome
import kotlinx.serialization.Serializable
import org.apache.commons.validator.routines.EmailValidator

@JvmInline
@Serializable
value class EmailAddress private constructor(private val value: String) {

    init {
        require(isValidEmailAddress(value)) { "Email address `$value` is invalid" }
    }

    override fun toString() = value

    companion object {
        fun from(value: String) = EmailAddress(value)

        fun parse(value: String): Outcome<EmailAddress, InvalidEmailAddress> =
            if (isValidEmailAddress(value)) Outcome.Success(EmailAddress(value))
            else Outcome.Failure(InvalidEmailAddress(value))

        fun isValidEmailAddress(str: String) = EmailValidator.getInstance().isValid(str)
    }
}
