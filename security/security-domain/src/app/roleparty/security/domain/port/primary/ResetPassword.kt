package app.roleparty.security.domain.port.primary

import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.Outcome
import app.roleparty.security.domain.model.SecurityError
import java.util.Locale

interface ResetPassword {

    suspend fun request(emailAddress: EmailAddress, locale: Locale)

    suspend fun confirm(emailAddress: EmailAddress, code: String, newPassword: String): Outcome<Unit, SecurityError>
}
