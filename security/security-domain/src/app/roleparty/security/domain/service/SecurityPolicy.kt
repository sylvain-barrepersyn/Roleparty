package app.roleparty.security.domain.service

import app.roleparty.communication.commons.EmailAddress
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

internal val PASSWORD_LENGTH = 8..128
internal val ACCESS_TOKEN_DURATION = 15.minutes
internal val REFRESH_TOKEN_DURATION = 14.days
internal val REFRESH_RETRY_GRACE = 30.seconds
internal val SIGN_UP_CODE_DURATION = 1.hours
internal val PASSWORD_RESET_CODE_DURATION = 15.minutes

internal const val MAX_FAILED_ATTEMPTS = 10
internal val FAILED_ATTEMPTS_WINDOW = 15.minutes
internal const val MAX_EMAILS = 3
internal val EMAILS_WINDOW = 1.hours

internal fun signInAttempts(emailAddress: EmailAddress) = "sign-in:$emailAddress"
