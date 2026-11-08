package app.roleparty.security.domain.port.primary

interface PurgeExpiredSecrets {

    suspend operator fun invoke()
}
