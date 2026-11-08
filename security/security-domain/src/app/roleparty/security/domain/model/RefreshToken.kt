package app.roleparty.security.domain.model

import java.security.SecureRandom
import kotlin.io.encoding.Base64

@JvmInline
value class RefreshToken private constructor(private val value: String) {
    override fun toString() = value

    fun hash() = SecretHash.of(value)

    companion object {
        private const val BYTES = 32
        private val random = SecureRandom()

        fun generate(): RefreshToken {
            val bytes = ByteArray(BYTES).also(random::nextBytes)
            return RefreshToken(Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT).encode(bytes))
        }

        fun from(value: String) = RefreshToken(value)
    }
}
