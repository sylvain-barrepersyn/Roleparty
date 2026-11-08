package app.roleparty.security.domain.port.primary

import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.Outcome
import app.roleparty.security.domain.model.SecurityError
import java.util.Locale

interface SignUp {

    suspend operator fun invoke(emailAddress: EmailAddress, password: String, locale: Locale): Outcome<Unit, SecurityError>
}
