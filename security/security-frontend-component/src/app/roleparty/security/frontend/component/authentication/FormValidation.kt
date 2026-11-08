package app.roleparty.security.frontend.component.authentication

import app.roleparty.communication.commons.EmailAddress
import app.roleparty.security.frontend.component.resources.Res
import app.roleparty.security.frontend.component.resources.invalid_email_address
import app.roleparty.security.frontend.component.resources.invalid_password
import app.roleparty.security.frontend.component.resources.missing_field
import app.roleparty.security.frontend.component.resources.passwords_differ
import org.jetbrains.compose.resources.StringResource

private val NEW_PASSWORD_LENGTH = 8..128

internal fun emailAddressError(value: String): StringResource? =
    if (EmailAddress.isValidEmailAddress(value.trim().lowercase())) null else Res.string.invalid_email_address

internal fun newPasswordError(value: String): StringResource? =
    if (value.length in NEW_PASSWORD_LENGTH) null else Res.string.invalid_password

internal fun confirmationError(password: String, confirmation: String): StringResource? =
    if (password == confirmation) null else Res.string.passwords_differ

internal fun requiredError(value: String): StringResource? =
    if (value.isNotBlank()) null else Res.string.missing_field
