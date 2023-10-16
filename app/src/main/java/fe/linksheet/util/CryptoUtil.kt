package fe.linksheet.util

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object CryptoUtil {
    data class HmacSha(val algorithm: String, val keySize: Int)

    private val secureRandom = SecureRandom()

    fun getRandomBytes(size: Int) = ByteArray(size).apply {
        secureRandom.nextBytes(this)
    }

    fun makeHmac(
        algorithm: String,
        key: ByteArray,
    ): Mac = Mac.getInstance(algorithm).apply {
        init(SecretKeySpec(key, algorithm))
    }

    fun sha256Hex(input: ByteArray): String {
        return MessageDigest.getInstance("SHA-256").digest(input)
            .fold(StringBuilder()) { sb, it -> sb.append("%02x".format(it)) }
            .toString()
    }
}