package app.roleparty.communication.commons

import kotlinx.serialization.Serializable
import org.apache.commons.validator.routines.EmailValidator

@JvmInline
@Serializable
value class EmailAddress private constructor(private val value: String) {

    init {
        require(isValidEmailAddress(value)) { "Email address `$value` is invalid" }
    }

    companion object {
        fun from(value: String) = EmailAddress(value)
        fun isValidEmailAddress(str: String) = EmailValidator.getInstance().isValid(str)
    }
}