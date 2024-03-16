package fe.linksheet.module.redactor

import fe.linksheet.extension.java.hash
import fe.stringbuilder.util.*
import java.io.IOException
import javax.crypto.Mac

interface SensitivePart<T> {
    interface Default<T> : SensitivePart<T> {
        override fun protect(raw: String, hmac: Mac): String {
            return hmac.hash(raw)
        }
    }

    fun asString(): String

    fun protect(raw: String, hmac: Mac): String
}

@JvmInline
value class SensitiveString(private val str: CharSequence) : SensitivePart.Default<CharSequence> {
    override fun asString(): String {
        return str.toString()
    }
}


sealed class ProtectedStringBuilder(
    private val stringBuilder: StringBuilder = StringBuilder()
) : Appendable by stringBuilder {
    data object Raw : ProtectedStringBuilder() {
        override fun <T> protect(sensitive: SensitivePart<T>): String {
            return sensitive.asString()
        }
    }

    data class Redacted(private val hmac: Mac) : ProtectedStringBuilder() {
        override fun <T> protect(sensitive: SensitivePart<T>): String {
            return sensitive.protect(sensitive.asString(), hmac)
        }
    }

    @Throws(IOException::class)
    override fun append(str: CharSequence?): ProtectedStringBuilder {
        stringBuilder.append(str)
        return this
    }

    fun append(prefix: String, any: Any): ProtectedStringBuilder {
        stringBuilder.append(prefix).append("=").append(any)
        return this
    }

    fun <T> sensitive(prefix: String, sensitive: SensitivePart<T>) {
        append(prefix).append("=").append(protect(sensitive))
    }

    protected abstract fun <T> protect(sensitive: SensitivePart<T>): String
}


typealias Package = SensitiveString
typealias StringUrl = SensitiveString
typealias Host = SensitiveString
typealias Clazz = SensitiveString
