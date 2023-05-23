package fe.linksheet.util

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
}