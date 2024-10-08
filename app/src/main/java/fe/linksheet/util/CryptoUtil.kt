package fe.linksheet.util

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
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

    fun md5(input: ByteArray): ByteArray {
        return MessageDigest.getInstance("MD5").digest(input)
    }

    fun sha256(input: ByteArray): ByteArray {
        return MessageDigest.getInstance("SHA-256").digest(input)
    }

    fun sha256Hex(input: ByteArray): String {
        return sha256(input)
            .fold(StringBuilder()) { sb, it -> sb.append("%02x".format(it)) }
            .toString()
    }

    fun generateKey(algorithm: String, size: Int): SecretKey {
        val generator = KeyGenerator.getInstance(algorithm)
        generator.init(size)

        return generator.generateKey()
    }

    fun deriveKey(algorithm: String, password: CharArray, salt: ByteArray, iterations: Int, size: Int): SecretKey {
        val factory = SecretKeyFactory.getInstance(algorithm)
        val keySpec = PBEKeySpec(password, salt, iterations, size)

        return factory.generateSecret(keySpec)
    }
}
