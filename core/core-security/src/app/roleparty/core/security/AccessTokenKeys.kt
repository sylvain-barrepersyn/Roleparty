package app.roleparty.core.security

import java.security.KeyFactory
import java.security.MessageDigest
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import kotlin.io.encoding.Base64

object AccessTokenKeys {
    const val ISSUER = "roleparty.app"

    private const val KEY_ID_LENGTH = 16
    private val PEM_ARMOR = Regex("-----[A-Z ]+-----")
    private val WHITESPACE = Regex("\\s")

    fun publicKey(encoded: String) =
        KeyFactory.getInstance("EC").generatePublic(X509EncodedKeySpec(decode(encoded))) as ECPublicKey

    fun privateKey(encoded: String) =
        KeyFactory.getInstance("EC").generatePrivate(PKCS8EncodedKeySpec(decode(encoded))) as ECPrivateKey

    fun idOf(key: ECPublicKey): String =
        MessageDigest.getInstance("SHA-256").digest(key.encoded).toHexString().take(KEY_ID_LENGTH)

    private fun decode(encoded: String) = Base64.decode(encoded.replace(PEM_ARMOR, "").replace(WHITESPACE, ""))
}
