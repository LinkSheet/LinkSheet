package fe.linksheet.module.redactor

import fe.linksheet.util.CryptoUtil

open class Redactor(val hasher: LogHasher) {
    fun <T> processToString(param: T, processor: HashProcessor<T>): String {
        return hash(StringBuilder(), param, processor).toString()
    }

    private fun check(builder: StringBuilder, prefix: String? = null, param: Any?): Boolean {
        if (prefix != null) {
            builder.append(prefix)
        }

        if (param == null) {
            builder.append("<null>")
        }

        return param != null
    }

    fun <T> process(
        builder: StringBuilder,
        param: T?,
        processor: HashProcessor<T>,
        prefix: String? = null
    ): StringBuilder {
        if (!check(builder, prefix, param)) return builder
        return hasher.hash(builder, param!!, processor)
    }

    fun <T> process(builder: StringBuilder, param: Redactable<T>?, prefix: String? = null): StringBuilder {
        if (!check(builder, prefix, param)) return builder
        return param!!.process(builder, this)
    }

    fun <T> hash(builder: StringBuilder, input: T, processor: HashProcessor<T>): StringBuilder {
        return hasher.hash(builder, input, processor)
    }


    companion object {
        val NoOp = Redactor(LogHasher.NoOpHasher)


        val hmac = CryptoUtil.HmacSha("HmacSHA256", 64)

        @OptIn(ExperimentalStdlibApi::class)
        fun createHmacKey(): String {
            return CryptoUtil.getRandomBytes(hmac.keySize).toHexString()
        }
    }
}

class DefaultRedactor(key: ByteArray) : Redactor(
    LogHasher.LogKeyHasher(CryptoUtil.makeHmac(hmac.algorithm, key))
) {


//    override fun <T> process(redact: Boolean, param: T, processor: HashProcessor<T>): String {
//        val hasher = if (redact) logKeyHasher else LogHasher.NoOpHasher
//        return hasher.hash(StringBuilder(), param, processor).toString().replace("%", "%%")
////
////        return processor.process(StringBuilder(), param, processor)
////
////        return LogDumpable.dumpObject(StringBuilder(), hasher, param, processor)
////            .toString()
////            .replace("%", "%%")
//    }


}
