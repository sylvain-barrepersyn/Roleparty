package app.roleparty.security.domain.model

import java.security.MessageDigest
import java.security.SecureRandom
import kotlin.uuid.Uuid

@JvmInline
value class PasswordHash private constructor(private val value: String) {
    override fun toString() = value

    companion object {
        fun from(value: String) = PasswordHash(value)
    }
}

@JvmInline
value class SecretHash private constructor(private val value: String) {
    override fun toString() = value

    companion object {
        fun of(secret: String) = SecretHash(MessageDigest.getInstance("SHA-256").digest(secret.toByteArray()).toHexString())
        fun from(value: String) = SecretHash(value)
    }
}

@JvmInline
value class DeviceId private constructor(private val value: Uuid) {
    override fun toString() = value.toString()

    fun toUuid() = value

    companion object {
        fun generate() = DeviceId(Uuid.generateV7())
        fun from(value: Uuid) = DeviceId(value)
    }
}

internal object SecretGenerator {
    private const val EMAIL_CODE_LENGTH = 8
    private const val EMAIL_CODE_ALPHABET = "ABCDEFGHJKMNPQRSTUVWXYZ23456789"

    private val random = SecureRandom()

    fun emailCode(): String =
        String(CharArray(EMAIL_CODE_LENGTH) { EMAIL_CODE_ALPHABET[random.nextInt(EMAIL_CODE_ALPHABET.length)] })
}
